package com.eygraber.portal

import com.eygraber.portal.internal.PortalBackstackEntry
import com.eygraber.portal.internal.PortalBackstackEntryBuilder
import com.eygraber.portal.internal.PortalBackstackMutation
import com.eygraber.portal.internal.PortalEntryBuilder
import com.eygraber.portal.internal.PortalState
import kotlinx.coroutines.flow.Flow

public interface ReadOnlyBackstack<KeyT> {
  public val size: Int

  public val changes: Flow<List<PortalBackstackEntry<KeyT>>>

  public operator fun contains(backstackEntryId: String): Boolean

  public fun peek(): String?
}

public interface PortalBackstack<KeyT> : ReadOnlyBackstack<KeyT> {
  public fun push(
    backstackEntryId: String,
    @PortalTransactionBuilderDsl builder: PushBuilder<KeyT>.() -> Unit
  )

  public fun pop(
    inclusive: Boolean = true,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null,
    popPredicate: (PortalBackstackEntry<KeyT>) -> Boolean = { false }
  ): Boolean

  public fun clear(
    suppressTransitions: Boolean = true,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null
  ): Boolean

  public interface PushBuilder<KeyT> {
    public fun add(
      portal: KeyedPortal<out KeyT>,
      isAttachedToComposition: Boolean = true,
      transitionOverride: EnterTransitionOverride? = null
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

  override val changes = portalState.backstackEntriesUpdateFlow()

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
    inclusive: Boolean,
    enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)?,
    exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)?,
    popPredicate: (PortalBackstackEntry<KeyT>) -> Boolean
  ): Boolean = portalState.transactWithBackstack { backstackStack ->
    val originalSize = backstackStack.size
    var stop = false
    do {
      tryToActuallyPopBackstack(
        backstackStack,
        enterTransitionOverride,
        exitTransitionOverride
      ) { entryToPop ->
        if(!popPredicate(entryToPop)) {
          stop = true

          inclusive
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
  keyMapper: (KeyT) -> String = { it.toString() },
  builder: PortalBackstack.PushBuilder<KeyT>.() -> Unit
) {
  push(keyMapper(backstackEntryId), builder)
}

public fun <KeyT> PortalBackstack<KeyT>.popUntil(
  backstackEntryId: String,
  inclusive: Boolean = true,
  enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
  exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null
): Boolean = pop(
  inclusive,
  enterTransitionOverride,
  exitTransitionOverride
) { entry ->
  entry.id != backstackEntryId
}

public fun <KeyT> PortalBackstack<KeyT>.popUntil(
  backstackEntryId: KeyT,
  keyMapper: (KeyT) -> String = { it.toString() },
  inclusive: Boolean = true,
  enterTransitionOverride: ((KeyT) -> EnterTransitionOverride?)? = null,
  exitTransitionOverride: ((KeyT) -> ExitTransitionOverride?)? = null
): Boolean = popUntil(
  keyMapper(backstackEntryId),
  inclusive,
  enterTransitionOverride,
  exitTransitionOverride
)

public fun <KeyT> PortalBackstack<KeyT>.contains(
  backstackEntryId: KeyT,
  keyMapper: (KeyT) -> String = { it.toString() }
): Boolean = keyMapper(backstackEntryId) in this

public fun <KeyT> PortalBackstack<KeyT>.isTop(
  backstackEntryId: String
): Boolean = peek() == backstackEntryId

public fun <KeyT> PortalBackstack<KeyT>.isTop(
  backstackEntryId: KeyT,
  keyMapper: (KeyT) -> String = { it.toString() },
): Boolean = isTop(keyMapper(backstackEntryId))

public val ReadOnlyBackstack<*>.isEmpty: Boolean get() = size == 0
public val ReadOnlyBackstack<*>.isNotEmpty: Boolean get() = size > 0
