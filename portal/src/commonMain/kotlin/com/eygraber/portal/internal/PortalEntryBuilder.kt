package com.eygraber.portal.internal

import com.eygraber.portal.DisappearingPortalEntry
import com.eygraber.portal.EnterTransitionOverride
import com.eygraber.portal.ExitTransitionOverride
import com.eygraber.portal.KeyedPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalBackstackState
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalManagerValidation
import com.eygraber.portal.PortalRemovedListener
import com.eygraber.portal.PortalRendererState
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

internal class PortalEntryBuilder<KeyT>(
  override val backstack: PortalBackstack<KeyT>,
  private val transactionPortalEntries: MutableList<PortalEntry<KeyT>>,
  private val transactionDisappearingPortalEntries: MutableList<DisappearingPortalEntry<KeyT>>,
  private val transactionBackstackEntries: MutableList<PortalBackstackEntry<KeyT>>,
  private val backstackState: PortalBackstackState,
  private val validation: PortalManagerValidation,
) : PortalManager.EntryBuilder<KeyT> {
  private val _postTransactionOps = atomic(emptyList<() -> Unit>())
  internal val postTransactionOps get() = _postTransactionOps.value

  override val size get() = transactionPortalEntries.size

  override val portalEntries: List<PortalEntry<KeyT>> get() = transactionPortalEntries

  class Payload<KeyT>(
    val entries: List<PortalEntry<KeyT>>,
    val disappearingEntries: List<DisappearingPortalEntry<KeyT>>,
    val backstackEntries: List<PortalBackstackEntry<KeyT>>,
  )

  override fun contains(key: KeyT) =
    transactionPortalEntries.findLast { entry ->
      entry.key == key
    } != null

  override fun contains(uid: PortalEntry.Id) =
    transactionPortalEntries.findLast { entry ->
      entry.uid == uid
    } != null

  override fun add(
    portal: KeyedPortal<out KeyT>,
    isAttachedToComposition: Boolean,
    transitionOverride: EnterTransitionOverride?,
  ) = PortalEntry(
    portal = portal,
    wasContentPreviouslyVisible = false,
    backstackState = PortalBackstackState.None,
    rendererState = when {
      isAttachedToComposition -> PortalRendererState.Added
      else -> PortalRendererState.Detached
    },
    enterTransitionOverride = transitionOverride,
    exitTransitionOverride = null,
    uid = PortalEntry.Id.generate(),
  ).also {
    transactionPortalEntries += it
  }

  override fun attachToComposition(
    key: KeyT,
    transitionOverride: EnterTransitionOverride?,
  ) = transactionPortalEntries.findLast { entry ->
    entry.key == key
  }?.let {
    attachToComposition(it.uid, transitionOverride)
  }

  override fun attachToComposition(
    uid: PortalEntry.Id,
    transitionOverride: EnterTransitionOverride?,
  ) = uid.applyMutationToPortalEntries { entry, _ ->
    entry.copy(
      wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
      backstackState = backstackState,
      rendererState = PortalRendererState.Attached,
      enterTransitionOverride = transitionOverride,
      exitTransitionOverride = null,
    )
  }

  override fun detachFromComposition(
    key: KeyT,
    transitionOverride: ExitTransitionOverride?,
  ) = transactionPortalEntries.findLast { entry ->
    entry.key == key
  }?.let {
    detachFromComposition(it.uid, transitionOverride)
  }

  override fun detachFromComposition(
    uid: PortalEntry.Id,
    transitionOverride: ExitTransitionOverride?,
  ) = uid.applyMutationToPortalEntries { entry, _ ->
    entry.copy(
      wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
      backstackState = backstackState,
      rendererState = PortalRendererState.Detached,
      enterTransitionOverride = null,
      exitTransitionOverride = transitionOverride,
    )
  }

  override fun remove(
    key: KeyT,
    transitionOverride: ExitTransitionOverride?,
  ) = transactionPortalEntries.findLast { entry ->
    entry.key == key
  }?.let {
    remove(it.uid, transitionOverride)
  }

  override fun remove(
    uid: PortalEntry.Id,
    transitionOverride: ExitTransitionOverride?,
  ) = uid.applyMutationToPortalEntries { entry, index ->
    val insertionIndex = transactionDisappearingPortalEntries.findInsertionIndex(
      forIndex = index,
    )

    transactionDisappearingPortalEntries.add(
      insertionIndex,
      DisappearingPortalEntry(
        entry = entry.copy(
          wasContentPreviouslyVisible = entry.rendererState.isAddedOrAttached,
          backstackState = PortalBackstackState.None,
          rendererState = PortalRendererState.Removed,
          enterTransitionOverride = null,
          exitTransitionOverride = transitionOverride,
        ),
        index = index,
      ),
    ).also {
      entry.portal.notifyOfRemoval(
        isCompletelyRemoved = false,
      )
    }

    // returning null removes this entry from transactionPortalEntries
    null
  }

  override fun clear(
    clearDisappearingEntries: Boolean,
    transitionOverrideProvider: ((KeyT) -> ExitTransitionOverride?)?,
  ) {
    if(clearDisappearingEntries) {
      transactionDisappearingPortalEntries.forEach { disappearingEntry ->
        disappearingEntry.entry.portal.notifyOfRemoval(
          isCompletelyRemoved = true,
        )
      }

      transactionDisappearingPortalEntries.clear()
    }

    transactionPortalEntries.reversed().forEach { entry ->
      remove(
        uid = entry.uid,
        transitionOverride = transitionOverrideProvider?.invoke(entry.key),
      )
    }

    // backstack cleanup
    transactionBackstackEntries.clear()
  }

  internal fun disappear(
    uid: PortalEntry.Id,
  ) {
    val indexToRemove = transactionDisappearingPortalEntries.indexOfLast { disappearingEntry ->
      disappearingEntry.entry.uid == uid
    }
    if(indexToRemove >= 0) {
      val disappearingEntry = transactionDisappearingPortalEntries.removeAt(indexToRemove)

      disappearingEntry.entry.portal.notifyOfRemoval(
        isCompletelyRemoved = true,
      )
    }
  }

  internal fun build(): Payload<KeyT> = Payload(
    entries = transactionPortalEntries,
    disappearingEntries = transactionDisappearingPortalEntries,
    backstackEntries = transactionBackstackEntries,
  )

  @Suppress("UnusedReceiverParameter")
  internal inline fun <R> PortalBackstack<KeyT>.usingBackstack(
    backstackState: PortalBackstackState,
    builder: PortalEntryBuilder<KeyT>.(
      MutableList<PortalBackstackEntry<KeyT>>,
    ) -> R,
  ) = PortalEntryBuilder(
    transactionPortalEntries = transactionPortalEntries,
    transactionDisappearingPortalEntries = transactionDisappearingPortalEntries,
    transactionBackstackEntries = transactionBackstackEntries,
    backstack = backstack,
    backstackState = backstackState,
    validation = validation,
  ).builder(transactionBackstackEntries)

  private fun PortalEntry.Id.applyMutationToPortalEntries(
    entryMatcher: (PortalEntry<KeyT>) -> Boolean = { it.uid == this },
    mutate: (PortalEntry<KeyT>, Int) -> PortalEntry<KeyT>?,
  ) = transactionPortalEntries
    .indexOfLast { entry -> entryMatcher(entry) }
    .takeIf { it > -1 }
    ?.let { index ->
      val entry = transactionPortalEntries[index]

      mutate(entry, index).also { newEntry ->
        if(newEntry == null) {
          transactionPortalEntries.removeAt(index)
        }
        else {
          transactionPortalEntries[index] = newEntry
        }
      }
    }

  private fun Portal.notifyOfRemoval(
    isCompletelyRemoved: Boolean,
  ) {
    _postTransactionOps.update { oldPostTransactionOps ->
      oldPostTransactionOps +
        {
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

private fun <KeyT> List<DisappearingPortalEntry<KeyT>>.findInsertionIndex(
  forIndex: Int,
): Int {
  val searchIndex = binarySearchBy(forIndex) {
    it.index
  }

  return when {
    searchIndex >= 0 -> {
      // insert after any existing index matches
      // because we're assuming that anything disappearing
      // at the same index at a later time was above the original entry
      var insertionIndex = searchIndex + 1
      while(insertionIndex <= lastIndex) {
        if(this[insertionIndex].index == forIndex) {
          insertionIndex++
        }
      }

      insertionIndex
    }

    else -> -searchIndex - 1
  }
}

private fun ParentPortal.notifyChildrenOfRemoval(
  isCompletelyRemoved: Boolean,
) {
  for(manager in portalManagers) {
    for(entry in manager.portalEntries) {
      val childPortal = entry.portal

      if(childPortal is ParentPortal) {
        childPortal.notifyChildrenOfRemoval(isCompletelyRemoved)
      }

      if(childPortal is PortalRemovedListener) {
        childPortal.onPortalRemoved(isCompletelyRemoved)
      }
    }
  }
}
