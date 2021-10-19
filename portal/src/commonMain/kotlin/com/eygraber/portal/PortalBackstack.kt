package com.eygraber.portal

import com.eygraber.portal.internal.PortalBackstackEntry
import com.eygraber.portal.internal.PortalBackstackEntryBuilder
import com.eygraber.portal.internal.PortalBackstackMutation
import com.eygraber.portal.internal.PortalEntryBuilder
import com.eygraber.portal.internal.PortalState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@DslMarker
internal annotation class BackstackDsl

public interface ReadOnlyBackstack {
  public val size: Int

  public val changes: Flow<Unit>

  public operator fun contains(backstackEntryId: String): Boolean

  public fun peek(): String?
}

public interface PortalBackstack<PortalKey> : ReadOnlyBackstack {
  public fun push(
    backstackEntryId: String,
    @BackstackDsl builder: PushBuilder<PortalKey>.() -> Unit
  )

  public fun pop(
    untilBackstackEntryId: String? = null,
    inclusive: Boolean = true,
    transitionsOverride: PortalTransitionsProvider<PortalKey> = null
  ): Boolean

  public fun clear(
    suppressTransitions: Boolean = true,
    transitionsOverride: PortalTransitionsProvider<PortalKey> = null
  ): Boolean

  public interface PushBuilder<PortalKey> {
    public fun add(
      key: PortalKey,
      isAttachedToComposition: Boolean = true,
      transitionsOverride: PortalTransitions? = null,
      render: PortalRender,
    )

    public fun attachToComposition(
      key: PortalKey,
      transitionsOverride: PortalTransitions? = null
    )

    public fun detachFromComposition(
      key: PortalKey,
      transitionsOverride: PortalTransitions? = null
    )
  }
}

internal class PortalBackstackImpl<PortalKey>(
  private val portalState: PortalState<PortalKey>
) : PortalBackstack<PortalKey> {
  override val size: Int get() = portalState.backstackEntries.size

  override val changes = portalState.backstackEntriesUpdateFlow().map {}

  override fun contains(backstackEntryId: String) =
    portalState.backstackEntries.indexOfLast { it.id == backstackEntryId } >= 0

  override fun peek() = portalState.backstackEntries.lastOrNull()?.id

  override fun push(
    backstackEntryId: String,
    builder: PortalBackstack.PushBuilder<PortalKey>.() -> Unit
  ) {
    portalState.transactWithBackstack { backstackStack ->
      val backstackMutations =
        PortalBackstackEntryBuilder(this)
          .apply(builder)
          .build()

      backstackStack += listOf(
        PortalBackstackEntry(
          id = backstackEntryId,
          mutations = backstackMutations
        )
      )
    }
  }

  override fun clear(
    suppressTransitions: Boolean,
    transitionsOverride: PortalTransitionsProvider<PortalKey>
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size

    val actualTransitionOverride = when {
      suppressTransitions -> { _: PortalKey -> PortalTransitions.None }
      else -> transitionsOverride
    }

    backstackStack
      .reversed()
      .map(PortalBackstackEntry<PortalKey>::mutations)
      .forEach { mutations ->
        applyBackstackMutations(mutations.reversed(), actualTransitionOverride)
      }

    backstackStack.clear()

    originalSize > backstackStack.size
  }

  override fun pop(
    untilBackstackEntryId: String?,
    inclusive: Boolean,
    transitionsOverride: PortalTransitionsProvider<PortalKey>
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size
    var stop = false
    do {
      tryToActuallyPopBackstack(backstackStack, transitionsOverride) { entryToPop ->
        if(untilBackstackEntryId == null || entryToPop.id == untilBackstackEntryId) {
          stop = true

          untilBackstackEntryId == null || inclusive
        }
        else {
          true
        }
      }
    } while(!stop && backstackStack.size > 0)

    originalSize > backstackStack.size
  }

  private fun PortalEntryBuilder<PortalKey>.tryToActuallyPopBackstack(
    backstackStack: MutableList<PortalBackstackEntry<PortalKey>>,
    transitionsOverride: PortalTransitionsProvider<PortalKey>,
    popPredicate: (PortalBackstackEntry<PortalKey>) -> Boolean
  ) {
    when(val peek = backstackStack.lastOrNull()) {
      null -> Unit

      else -> when {
        popPredicate(peek) -> {
          backstackStack.removeLast()

          applyBackstackMutations(peek.mutations, transitionsOverride)
        }
      }
    }
  }

  private inline fun <R> PortalState<PortalKey>.transactWithBackstack(
    crossinline block: PortalEntryBuilder<PortalKey>.(MutableList<PortalBackstackEntry<PortalKey>>) -> R
  ) = transact {
    usingBackstack(block)
  }
}

private fun <PortalKey> PortalEntryBuilder<PortalKey>.applyBackstackMutations(
  mutations: List<PortalBackstackMutation<PortalKey>>,
  actualTransitionsOverride: PortalTransitionsProvider<PortalKey>
) {
  mutations.forEach { mutation ->
    when(mutation) {
      is PortalBackstackMutation.Remove -> remove(
        key = mutation.key,
        transitionsOverride = actualTransitionsOverride?.invoke(mutation.key) ?: mutation.transitionsOverride
      )

      is PortalBackstackMutation.AttachToComposition -> attachToComposition(
        key = mutation.key,
        transitionsOverride = actualTransitionsOverride?.invoke(mutation.key) ?: mutation.transitionsOverride
      )

      is PortalBackstackMutation.DetachFromComposition -> detachFromComposition(
        key = mutation.key,
        transitionsOverride = actualTransitionsOverride?.invoke(mutation.key) ?: mutation.transitionsOverride
      )

      is PortalBackstackMutation.Disappearing -> disappear(
        key = mutation.key
      )
    }
  }
}

public fun <PortalKey> PortalBackstack<PortalKey>.push(
  backstackEntryId: PortalKey,
  builder: PortalBackstack.PushBuilder<PortalKey>.() -> Unit
) {
  push(backstackEntryId.toString(), builder)
}

public fun <PortalKey> PortalBackstack<PortalKey>.pop(
  untilBackstackEntryId: PortalKey? = null,
  inclusive: Boolean = true,
  transitionsOverride: PortalTransitionsProvider<PortalKey> = null
): Boolean = pop(untilBackstackEntryId.toString(), inclusive, transitionsOverride)

public operator fun <PortalKey> PortalBackstack<PortalKey>.contains(
  backstackEntryId: PortalKey
): Boolean = backstackEntryId.toString() in this

public fun <PortalKey> PortalBackstack<PortalKey>.isTop(
  backstackEntryId: String
): Boolean = peek() == backstackEntryId

public fun <PortalKey> PortalBackstack<PortalKey>.isTop(
  backstackEntryId: PortalKey
): Boolean = isTop(backstackEntryId.toString())
