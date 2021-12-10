package com.eygraber.portal.internal

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalCompositionState
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalManagerValidation
import com.eygraber.portal.PortalRemovedListener
import com.eygraber.portal.PortalTransactionBuilderDsl
import com.eygraber.portal.PortalTransition
import kotlinx.atomicfu.atomic

internal class PortalEntryBuilder<PortalKey>(
  override val backstack: PortalBackstack<PortalKey>,
  private val transactionPortalEntries: MutableList<PortalEntry<PortalKey>>,
  private val transactionBackstackEntries: MutableList<PortalBackstackEntry<PortalKey>>,
  private val isForBackstack: Boolean,
  private val validation: PortalManagerValidation
) : PortalManager.EntryBuilder<PortalKey> {
  private val _postTransactionOps = atomic(emptyList<() -> Unit>())
  internal val postTransactionOps get() = _postTransactionOps.value

  override val size get() = transactionPortalEntries.filterNot { it.isDisappearing }.size

  override val portals: List<Pair<PortalKey, Portal>> get() = transactionPortalEntries.map { it.key to it.portal }

  override fun contains(key: PortalKey) =
    transactionPortalEntries.findLast { entry ->
      entry.key == key
    } != null

  override fun add(
    key: PortalKey,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransition?,
    portal: Portal
  ) {
    transactionPortalEntries += PortalEntry(
      key = key,
      wasContentPreviouslyVisible = false,
      isDisappearing = false,
      isBackstackMutation = false,
      compositionState = when {
        isAttachedToComposition -> PortalCompositionState.Added
        else -> PortalCompositionState.Detached
      },
      transitionOverride = transitionOverride?.let { enterTransition ->
        PortalTransition(
          enter = enterTransition,
          exit = ExitTransition.None
        )
      },
      portal = portal
    )
  }

  override fun attachToComposition(
    key: PortalKey,
    transitionOverride: EnterTransition?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot attach if the entry is disappearing")
          else -> entry
        }

        else -> entry.copy(
          wasContentPreviouslyVisible = entry.isAttachedToComposition,
          isBackstackMutation = isForBackstack,
          compositionState = PortalCompositionState.Attached,
          transitionOverride = transitionOverride?.let { enterTransition ->
            PortalTransition(
              enter = enterTransition,
              exit = ExitTransition.None
            )
          }
        )
      }
    }
  }

  override fun detachFromComposition(
    key: PortalKey,
    transitionOverride: ExitTransition?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot detach if the entry is disappearing")
          else -> entry
        }
        else -> entry.copy(
          wasContentPreviouslyVisible = entry.isAttachedToComposition,
          isBackstackMutation = isForBackstack,
          compositionState = PortalCompositionState.Detached,
          transitionOverride = transitionOverride?.let { exitTransition ->
            PortalTransition(
              enter = EnterTransition.None,
              exit = exitTransition
            )
          }
        )
      }
    }
  }

  override fun remove(
    key: PortalKey,
    transitionOverride: ExitTransition?
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
          wasContentPreviouslyVisible = entry.isAttachedToComposition,
          isBackstackMutation = false,
          compositionState = PortalCompositionState.Removed,
          isDisappearing = true,
          transitionOverride = transitionOverride?.let { exitTransition ->
            PortalTransition(
              enter = EnterTransition.None,
              exit = exitTransition
            )
          }
        ).also {
          entry.portal.notifyOfRemoval(
            isCompletelyRemoved = false
          )
        }
      }
    }
  }

  override fun clear(
    transitionsOverrideProvider: ((PortalKey) -> ExitTransition?)?
  ) {
    transactionPortalEntries.reversed().forEach { entry ->
      remove(
        key = entry.key,
        transitionOverride = transitionsOverrideProvider?.invoke(entry.key)
      )
    }
  }

  internal fun disappear(
    key: PortalKey
  ) {
    key.applyMutationToPortalEntries { entry ->
      if(entry.isDisappearing) {
        _postTransactionOps.value = _postTransactionOps.value.plus {
          entry.portal.notifyOfRemoval(
            isCompletelyRemoved = true
          )
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

  internal fun build(): Pair<List<PortalEntry<PortalKey>>, List<PortalBackstackEntry<PortalKey>>> =
    transactionPortalEntries to transactionBackstackEntries

  @Suppress("unused")
  internal inline fun <R> PortalBackstack<PortalKey>.usingBackstack(
    @PortalTransactionBuilderDsl
    builder: PortalEntryBuilder<PortalKey>.(MutableList<PortalBackstackEntry<PortalKey>>) -> R
  ) = PortalEntryBuilder(
    transactionPortalEntries = transactionPortalEntries,
    transactionBackstackEntries = transactionBackstackEntries,
    backstack = backstack,
    isForBackstack = true,
    validation = validation
  ).builder(transactionBackstackEntries)

  private fun PortalKey.applyMutationToPortalEntries(
    mutate: (PortalEntry<PortalKey>) -> PortalEntry<PortalKey>?
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
    _postTransactionOps.value = _postTransactionOps.value.plus {
      if(this is ParentPortal) {
        notifyChildrenOfRemoval(isCompletelyRemoved = isCompletelyRemoved)
      }

      if(this is PortalRemovedListener) {
        onPortalRemoved(isCompletelyRemoved = isCompletelyRemoved)
      }
    }
  }
}

private fun ParentPortal.notifyChildrenOfRemoval(
  isCompletelyRemoved: Boolean
) {
  for(manager in portalManagers) {
    for(childPortal in manager.portals) {
      if(childPortal is ParentPortal) {
        notifyChildrenOfRemoval(isCompletelyRemoved)
      }

      if(childPortal is PortalRemovedListener) {
        childPortal.onPortalRemoved(isCompletelyRemoved)
      }
    }
  }
}
