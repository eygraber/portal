package com.eygraber.portal.internal

import com.eygraber.portal.AbstractPortalManager
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalManagerValidation
import com.eygraber.portal.PortalRemovedListener
import com.eygraber.portal.PortalRendererState
import com.eygraber.portal.PortalTransactionBuilderDsl
import kotlinx.atomicfu.atomic

internal class PortalEntryBuilder<KeyT, EntryT, ExtraT : Extra, PortalT : Portal>(
  override val backstack: PortalBackstack<KeyT, ExtraT, PortalT>,
  private val transactionPortalEntries: MutableList<EntryT>,
  private val transactionBackstackEntries: MutableList<PortalBackstackEntry<KeyT>>,
  private val isForBackstack: Boolean,
  private val validation: PortalManagerValidation,
  private val entryCallbacks: PortalEntry.Callbacks<KeyT, EntryT, ExtraT, PortalT>
) : AbstractPortalManager.EntryBuilder<KeyT, ExtraT, PortalT> where EntryT : Entry<KeyT, ExtraT, PortalT> {
  private val _postTransactionOps = atomic(emptyList<() -> Unit>())
  internal val postTransactionOps get() = _postTransactionOps.value

  override val size get() = transactionPortalEntries.filterNot { it.isDisappearing }.size

  override val portals: List<Pair<KeyT, Portal>> get() = transactionPortalEntries.map { it.key to it.portal }

  override fun contains(key: KeyT) =
    transactionPortalEntries.findLast { entry ->
      entry.key == key
    } != null

  override fun add(
    key: KeyT,
    isAttachedToComposition: Boolean,
    extra: ExtraT?,
    portal: PortalT
  ) {
    transactionPortalEntries += entryCallbacks.create(
      key = key,
      wasContentPreviouslyVisible = false,
      isDisappearing = false,
      isBackstackMutation = false,
      rendererState = when {
        isAttachedToComposition -> PortalRendererState.Added
        else -> PortalRendererState.Detached
      },
      extra = extra,
      portal = portal
    )
  }

  override fun attachToComposition(
    key: KeyT,
    extra: ExtraT?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot attach if the entry is disappearing")
          else -> entry
        }

        else -> entryCallbacks.attach(
          entry = entry,
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          isBackstackMutation = isForBackstack,
          rendererState = PortalRendererState.Attached,
          extra = extra
        )
      }
    }
  }

  override fun detachFromComposition(
    key: KeyT,
    extra: ExtraT?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          validation.validatePortalTransactions -> error("Cannot detach if the entry is disappearing")
          else -> entry
        }
        else -> entryCallbacks.detach(
          entry = entry,
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          isBackstackMutation = isForBackstack,
          rendererState = PortalRendererState.Detached,
          extra = extra
        )
      }
    }
  }

  override fun remove(
    key: KeyT,
    extra: ExtraT?
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

        else -> entryCallbacks.remove(
          entry = entry,
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          isBackstackMutation = false,
          rendererState = PortalRendererState.Removed,
          isDisappearing = true,
          extra = extra
        ).also {
          entry.portal.notifyOfRemoval(
            isCompletelyRemoved = false
          )
        }
      }
    }
  }

  override fun clear(
    extraProvider: ((KeyT) -> ExtraT?)?
  ) {
    transactionPortalEntries.reversed().forEach { entry ->
      remove(
        key = entry.key,
        extra = extraProvider?.invoke(entry.key)
      )
    }
  }

  internal fun disappear(
    key: KeyT
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

  internal fun build(): Pair<List<EntryT>, List<PortalBackstackEntry<KeyT>>> =
    transactionPortalEntries to transactionBackstackEntries

  @Suppress("unused")
  internal inline fun <R> PortalBackstack<KeyT, ExtraT, PortalT>.usingBackstack(
    @PortalTransactionBuilderDsl
    builder: PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>.(MutableList<PortalBackstackEntry<KeyT>>) -> R
  ) = PortalEntryBuilder(
    transactionPortalEntries = transactionPortalEntries,
    transactionBackstackEntries = transactionBackstackEntries,
    backstack = backstack,
    isForBackstack = true,
    validation = validation,
    entryCallbacks = entryCallbacks
  ).builder(transactionBackstackEntries)

  private fun KeyT.applyMutationToPortalEntries(
    mutate: (EntryT) -> EntryT?
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
