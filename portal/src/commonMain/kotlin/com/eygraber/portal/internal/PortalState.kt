package com.eygraber.portal.internal

import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalBackstackState
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalManagerValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PortalState<KeyT>(
  private val validation: PortalManagerValidation
) {
  private val mutablePortalEntries = MutableStateFlow(emptyList<PortalEntry<KeyT>>())
  private val mutableBackstackEntries = MutableStateFlow(emptyList<PortalBackstackEntry<KeyT>>())

  private var transactionBuilder: PortalEntryBuilder<KeyT>? =
    null

  inline val portalEntries: List<PortalEntry<KeyT>> get() = mutablePortalEntries.value
  inline val backstackEntries: List<PortalBackstackEntry<KeyT>> get() = mutableBackstackEntries.value

  fun portalEntriesUpdateFlow(): StateFlow<List<PortalEntry<KeyT>>> = mutablePortalEntries
  fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<KeyT>>> = mutableBackstackEntries

  fun restoreState(
    entries: List<PortalEntry<KeyT>>,
    backstack: List<PortalBackstackEntry<KeyT>>
  ) {
    mutablePortalEntries.value = entries
    mutableBackstackEntries.value = backstack
  }

  fun startTransaction(backstack: PortalBackstack<KeyT>): Boolean {
    // reentrant
    if(transactionBuilder != null) return false

    transactionBuilder = PortalEntryBuilder(
      backstack = backstack,
      transactionPortalEntries = mutablePortalEntries.value.toMutableList(),
      transactionBackstackEntries = mutableBackstackEntries.value.toMutableList(),
      backstackState = PortalBackstackState.None,
      validation = validation
    )

    return true
  }

  fun <R> transact(
    builder: PortalEntryBuilder<KeyT>.() -> R
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
