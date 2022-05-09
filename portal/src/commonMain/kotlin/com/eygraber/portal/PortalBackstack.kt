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

public interface PortalBackstack<KeyT> : ReadOnlyBackstack {
  public fun push(
    backstackEntryId: String,
    @BackstackDsl builder: PushBuilder<KeyT>.() -> Unit
  )

  public fun pop(
    untilBackstackEntryId: String? = null,
    inclusive: Boolean = true,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null
  ): Boolean

  public fun clear(
    suppressTransitions: Boolean = true,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null
  ): Boolean

  public interface PushBuilder<KeyT> {
    public fun add(
      key: KeyT,
      isAttachedToComposition: Boolean = true,
      transitionOverride: EnterTransitionOverride? = null,
      portal: Portal
    )

    public fun attachToComposition(
      key: KeyT,
      transitionOverride: EnterTransitionOverride? = null
    )

    public fun detachFromComposition(
      key: KeyT,
      transitionOverride: ExitTransitionOverride? = null
    )
  }
}

internal class PortalBackstackImpl<KeyT>(
  private val portalState: PortalState<KeyT>
) : PortalBackstack<KeyT> {
  override val size: Int get() = portalState.backstackEntries.size

  override val changes = portalState.backstackEntriesUpdateFlow().map {}

  override fun contains(backstackEntryId: String) =
    portalState.backstackEntries.indexOfLast { it.id == backstackEntryId } >= 0

  override fun peek() = portalState.backstackEntries.lastOrNull()?.id

  override fun push(
    backstackEntryId: String,
    builder: PortalBackstack.PushBuilder<KeyT>.() -> Unit
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
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)?,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)?
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size

    backstackStack
      .reversed()
      .forEach { entry ->
        applyBackstackMutations(
          entry.mutations.reversed(),
          enterTransitionOverride,
          exitTransitionOverride
        )
      }

    backstackStack.clear()

    originalSize > backstackStack.size
  }

  override fun pop(
    untilBackstackEntryId: String?,
    inclusive: Boolean,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)?,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)?
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size
    var stop = false
    do {
      tryToActuallyPopBackstack(
        backstackStack,
        enterTransitionOverride,
        exitTransitionOverride
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

  private fun PortalEntryBuilder<KeyT>.tryToActuallyPopBackstack(
    backstackStack: MutableList<PortalBackstackEntry<KeyT>>,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)?,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)?,
    popPredicate: (PortalBackstackEntry<KeyT>) -> Boolean
  ) {
    when(val peek = backstackStack.lastOrNull()) {
      null -> Unit

      else -> when {
        popPredicate(peek) -> {
          backstackStack.removeLast()

          applyBackstackMutations(
            peek.mutations,
            enterTransitionOverride,
            exitTransitionOverride
          )
        }
      }
    }
  }

  private inline fun <R> PortalState<KeyT>.transactWithBackstack(
    crossinline block: PortalEntryBuilder<KeyT>.(MutableList<PortalBackstackEntry<KeyT>>) -> R
  ) = transact {
    usingBackstack(block)
  }
}

private fun <KeyT> PortalEntryBuilder<KeyT>.applyBackstackMutations(
  mutations: List<PortalBackstackMutation<KeyT>>,
  enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)?,
  exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)?
) {
  mutations.forEach { mutation ->
    when(mutation) {
      is PortalBackstackMutation.Remove -> remove(
        key = mutation.key,
        transitionOverride = exitTransitionOverride?.invoke(mutation.key)
      )

      is PortalBackstackMutation.Attach -> attachToComposition(
        key = mutation.key,
        transitionOverride = enterTransitionOverride?.invoke(mutation.key)
      )

      is PortalBackstackMutation.Detach -> detachFromComposition(
        key = mutation.key,
        transitionOverride = exitTransitionOverride?.invoke(mutation.key)
      )

      is PortalBackstackMutation.Disappearing -> disappear(
        key = mutation.key
      )
    }
  }
}

public fun <KeyT> PortalBackstack<KeyT>.push(
  backstackEntryId: KeyT,
  builder: PortalBackstack.PushBuilder<KeyT>.() -> Unit
) {
  push(backstackEntryId.toString(), builder)
}

public fun <KeyT> PortalBackstack<KeyT>.pop(
  untilBackstackEntryId: KeyT? = null,
  inclusive: Boolean = true,
  enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
  exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null
): Boolean = pop(
  untilBackstackEntryId.toString(),
  inclusive,
  enterTransitionOverride,
  exitTransitionOverride
)

public fun <KeyT> PortalBackstack<KeyT>.contains(
  backstackEntryId: KeyT
): Boolean = backstackEntryId.toString() in this

public fun <KeyT> PortalBackstack<KeyT>.isTop(
  backstackEntryId: String
): Boolean = peek() == backstackEntryId

public fun <KeyT> PortalBackstack<KeyT>.isTop(
  backstackEntryId: KeyT
): Boolean = isTop(backstackEntryId.toString())
