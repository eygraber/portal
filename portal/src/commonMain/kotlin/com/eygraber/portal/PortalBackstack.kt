package com.eygraber.portal

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
    enterTransitionsOverride: ((PortalKey) -> EnterTransition?)? = null,
    exitTransitionsOverride: ((PortalKey) -> ExitTransition?)? = null
  ): Boolean

  public fun clear(
    suppressTransitions: Boolean = true,
    enterTransitionsOverride: ((PortalKey) -> EnterTransition?)? = null,
    exitTransitionsOverride: ((PortalKey) -> ExitTransition?)? = null
  ): Boolean

  public interface PushBuilder<PortalKey> {
    public fun add(
      key: PortalKey,
      isAttachedToComposition: Boolean = true,
      transitionOverride: EnterTransition? = null,
      portal: Portal,
    )

    public fun attachToComposition(
      key: PortalKey,
      transitionOverride: EnterTransition? = null,
    )

    public fun detachFromComposition(
      key: PortalKey,
      transitionOverride: ExitTransition? = null,
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
    enterTransitionsOverride: ((PortalKey) -> EnterTransition?)?,
    exitTransitionsOverride: ((PortalKey) -> ExitTransition?)?
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size

    backstackStack
      .reversed()
      .map(PortalBackstackEntry<PortalKey>::mutations)
      .forEach { mutations ->
        applyBackstackMutations(
          mutations.reversed(),
          enterTransitionsOverride,
          exitTransitionsOverride
        )
      }

    backstackStack.clear()

    originalSize > backstackStack.size
  }

  override fun pop(
    untilBackstackEntryId: String?,
    inclusive: Boolean,
    enterTransitionsOverride: ((PortalKey) -> EnterTransition?)?,
    exitTransitionsOverride: ((PortalKey) -> ExitTransition?)?
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size
    var stop = false
    do {
      tryToActuallyPopBackstack(
        backstackStack,
        enterTransitionsOverride,
        exitTransitionsOverride
      ) { entryToPop ->
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
    enterTransitionsOverride: ((PortalKey) -> EnterTransition?)?,
    exitTransitionsOverride: ((PortalKey) -> ExitTransition?)?,
    popPredicate: (PortalBackstackEntry<PortalKey>) -> Boolean
  ) {
    when(val peek = backstackStack.lastOrNull()) {
      null -> Unit

      else -> when {
        popPredicate(peek) -> {
          backstackStack.removeLast()

          applyBackstackMutations(
            peek.mutations,
            enterTransitionsOverride,
            exitTransitionsOverride
          )
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
  enterTransitionsOverride: ((PortalKey) -> EnterTransition?)?,
  exitTransitionsOverride: ((PortalKey) -> ExitTransition?)?
) {
  mutations.forEach { mutation ->
    when(mutation) {
      is PortalBackstackMutation.Remove -> remove(
        key = mutation.key,
        transitionOverride = exitTransitionsOverride?.invoke(mutation.key)
      )

      is PortalBackstackMutation.AttachToComposition -> attachToComposition(
        key = mutation.key,
        transitionOverride = enterTransitionsOverride?.invoke(mutation.key)
      )

      is PortalBackstackMutation.DetachFromComposition -> detachFromComposition(
        key = mutation.key,
        transitionOverride = exitTransitionsOverride?.invoke(mutation.key)
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
  enterTransitionsOverride: ((PortalKey) -> EnterTransition?)? = null,
  exitTransitionsOverride: ((PortalKey) -> ExitTransition?)? = null
): Boolean = pop(
  untilBackstackEntryId.toString(),
  inclusive,
  enterTransitionsOverride,
  exitTransitionsOverride
)

public operator fun <PortalKey> PortalBackstack<PortalKey>.contains(
  backstackEntryId: PortalKey
): Boolean = backstackEntryId.toString() in this

public fun <PortalKey> PortalBackstack<PortalKey>.isTop(
  backstackEntryId: String
): Boolean = peek() == backstackEntryId

public fun <PortalKey> PortalBackstack<PortalKey>.isTop(
  backstackEntryId: PortalKey
): Boolean = isTop(backstackEntryId.toString())
