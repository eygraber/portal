package com.eygraber.portal.internal

import com.eygraber.portal.EnterTransitionOverride
import com.eygraber.portal.ExitTransitionOverride
import com.eygraber.portal.KeyedPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalManagerValidation
import com.eygraber.portal.PortalRemovedListener
import com.eygraber.portal.PortalRendererState
import com.eygraber.portal.PortalTransactionBuilderDsl
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

internal class PortalEntryBuilder<KeyT>(
  override val backstack: PortalBackstack<KeyT>,
  private val transactionPortalEntries: MutableList<PortalEntry<KeyT>>,
  private val transactionBackstackEntries: MutableList<PortalBackstackEntry<KeyT>>,
  private val isForBackstack: Boolean,
  private val validation: PortalManagerValidation
) : PortalManager.EntryBuilder<KeyT> {
  private val _postTransactionOps = atomic(emptyList<() -> Unit>())
  internal val postTransactionOps get() = _postTransactionOps.value

  override val size get() = transactionPortalEntries.filterNot { it.isDisappearing }.size

  override val portals: List<Pair<KeyT, Portal>> get() = transactionPortalEntries.map { it.key to it.portal }

  override fun contains(key: KeyT) =
    transactionPortalEntries.findLast { entry ->
      entry.key == key
    } != null

  override fun add(
    portal: KeyedPortal<KeyT>,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransitionOverride?
  ) {
    transactionPortalEntries += PortalEntry(
      portal = portal,
      wasContentPreviouslyVisible = false,
      isDisappearing = false,
      isBackstackMutation = false,
      rendererState = when {
        isAttachedToComposition -> PortalRendererState.Added
        else -> PortalRendererState.Detached
      },
      enterTransitionOverride = transitionOverride,
      exitTransitionOverride = null
    )
  }

  override fun attachToComposition(
    key: KeyT,
    transitionOverride: EnterTransitionOverride?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot attach if the entry is disappearing")
          else -> entry
        }

        else -> entry.copy(
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          isBackstackMutation = isForBackstack,
          rendererState = PortalRendererState.Attached,
          enterTransitionOverride = transitionOverride,
          exitTransitionOverride = null
        )
      }
    }
  }

  override fun detachFromComposition(
    key: KeyT,
    transitionOverride: ExitTransitionOverride?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot detach if the entry is disappearing")
          else -> entry
        }
        else -> entry.copy(
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          isBackstackMutation = isForBackstack,
          rendererState = PortalRendererState.Detached,
          enterTransitionOverride = null,
          exitTransitionOverride = transitionOverride
        )
      }
    }
  }

  override fun remove(
    key: KeyT,
    transitionOverride: ExitTransitionOverride?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot remove if the entry is disappearing")
          else -> null
        }.also {
          entry.portal.notifyOfRemoval(
            isCompletelyRemoved = true
          )
        }

        else -> entry.copy(
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          isBackstackMutation = false,
          rendererState = PortalRendererState.Removed,
          isDisappearing = true,
          enterTransitionOverride = null,
          exitTransitionOverride = transitionOverride
        ).also {
          entry.portal.notifyOfRemoval(
            isCompletelyRemoved = false
          )
        }
      }
    }
  }

  override fun clear(
    transitionOverrideProvider: ((KeyT) -> ExitTransitionOverride?)?
  ) {
    transactionPortalEntries.reversed().forEach { entry ->
      remove(
        key = entry.key,
        transitionOverride = transitionOverrideProvider?.invoke(entry.key)
      )
    }
  }

  internal fun disappear(
    key: KeyT
  ) {
    key.applyMutationToPortalEntries { entry ->
      if(entry.isDisappearing) {
        _postTransactionOps.update { oldPostTransactionOps ->
          oldPostTransactionOps + {
            entry.portal.notifyOfRemoval(
              isCompletelyRemoved = true
            )
          }
        }
        // removes the entry from portalEntries
        null
      }
      else {
        when {
          validation.validatePortalTransactions -> error("Cannot disappear if the entry isn't disappearing")
          else -> entry
        }
      }
    }
  }

  internal fun build(): Pair<List<PortalEntry<KeyT>>, List<PortalBackstackEntry<KeyT>>> =
    transactionPortalEntries to transactionBackstackEntries

  @Suppress("unused")
  internal inline fun <R> PortalBackstack<KeyT>.usingBackstack(
    @PortalTransactionBuilderDsl
    builder: PortalEntryBuilder<KeyT>.(
      MutableList<PortalBackstackEntry<KeyT>>
    ) -> R
  ) = PortalEntryBuilder(
    transactionPortalEntries = transactionPortalEntries,
    transactionBackstackEntries = transactionBackstackEntries,
    backstack = backstack,
    isForBackstack = true,
    validation = validation
  ).builder(transactionBackstackEntries)

  private fun KeyT.applyMutationToPortalEntries(
    mutate: (PortalEntry<KeyT>) -> PortalEntry<KeyT>?
  ) {
    transactionPortalEntries
      .indexOfLast { entry -> entry.key == this }
      .takeIf { it > -1 }
      ?.let { index ->
        val entry = transactionPortalEntries[index]

        val newEntry = mutate(entry)
        if(newEntry == null) {
          transactionPortalEntries.removeAt(index)
        }
        else {
          transactionPortalEntries[index] = newEntry
        }
      }
  }

  private fun Portal.notifyOfRemoval(
    isCompletelyRemoved: Boolean
  ) {
    _postTransactionOps.update { oldPostTransactionOps ->
      oldPostTransactionOps + {
        if(this is ParentPortal) {
          notifyChildrenOfRemoval(isCompletelyRemoved = isCompletelyRemoved)
        }

        if(this is PortalRemovedListener) {
          onPortalRemoved(isCompletelyRemoved = isCompletelyRemoved)
        }
      }
    }
  }
}

private fun ParentPortal.notifyChildrenOfRemoval(
  isCompletelyRemoved: Boolean
) {
  for(manager in portalManagers) {
    for((_, childPortal) in manager.portals) {
      if(childPortal is ParentPortal) {
        notifyChildrenOfRemoval(isCompletelyRemoved)
      }

      if(childPortal is PortalRemovedListener) {
        childPortal.onPortalRemoved(isCompletelyRemoved)
      }
    }
  }
}
