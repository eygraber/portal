package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalManagerValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PortalState<KeyT, EntryT : Entry<KeyT, ExtraT, PortalT>, ExtraT : Extra, PortalT : Portal>(
  private val validation: PortalManagerValidation,
  private val entryCallbacks: PortalEntry.Callbacks<KeyT, EntryT, ExtraT, PortalT>
) {
  private val mutablePortalEntries = MutableStateFlow(emptyList<EntryT>())
  private val mutableBackstackEntries = MutableStateFlow(emptyList<PortalBackstackEntry<KeyT>>())

  private var transactionBuilder: PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>? = null

  inline val portalEntries: List<EntryT> get() = mutablePortalEntries.value
  inline val backstackEntries: List<PortalBackstackEntry<KeyT>> get() = mutableBackstackEntries.value

  fun portalEntriesUpdateFlow(): StateFlow<List<EntryT>> = mutablePortalEntries
  fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<KeyT>>> = mutableBackstackEntries

  fun restoreState(
    entries: List<EntryT>,
    backstack: List<PortalBackstackEntry<KeyT>>
  ) {
    mutablePortalEntries.value = entries
    mutableBackstackEntries.value = backstack
  }

  fun startTransaction(backstack: PortalBackstack<KeyT, ExtraT, PortalT>) {
    // reentrant
    if(transactionBuilder != null) return

    transactionBuilder = PortalEntryBuilder(
      backstack = backstack,
      transactionPortalEntries = mutablePortalEntries.value.toMutableList(),
      transactionBackstackEntries = mutableBackstackEntries.value.toMutableList(),
      isForBackstack = false,
      validation = validation,
      entryCallbacks = entryCallbacks
    )
  }

  fun <R> transact(
    builder: PortalEntryBuilder<KeyT, EntryT, ExtraT, PortalT>.() -> R
  ) = requireNotNull(transactionBuilder) {
    "Cannot transact if not in a transaction"
  }.builder()

  fun rollbackTransaction() {
    transactionBuilder = null
  }

  fun commitTransaction() {
    transactionBuilder?.build()?.let { (newPortals, newBackstackStack) ->
      mutablePortalEntries.value = newPortals
      mutableBackstackEntries.value = newBackstackStack
    }
    transactionBuilder?.postTransactionOps?.forEach { it() }
    transactionBuilder = null
  }
}
