package com.eygraber.portal.internal

import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalBackstackState
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalManagerValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class PortalState<KeyT>(
  private val validation: PortalManagerValidation,
) {
  private val mutablePortalEntries = MutableStateFlow(
    PortalManager.Entries<KeyT>(
      entries = emptyList(),
      disappearingEntries = emptyList(),
    ),
  )
  private val mutableBackstackEntries = MutableStateFlow(emptyList<PortalBackstackEntry<KeyT>>())

  private var transactionBuilder: PortalEntryBuilder<KeyT>? =
    null

  inline val portalEntries: List<PortalEntry<KeyT>> get() = mutablePortalEntries.value.entries
  inline val backstackEntries: List<PortalBackstackEntry<KeyT>> get() = mutableBackstackEntries.value

  fun portalEntriesUpdateFlow(): StateFlow<PortalManager.Entries<KeyT>> = mutablePortalEntries
  fun backstackEntriesUpdateFlow(): StateFlow<List<PortalBackstackEntry<KeyT>>> = mutableBackstackEntries

  fun restoreState(
    entries: List<PortalEntry<KeyT>>,
    backstack: List<PortalBackstackEntry<KeyT>>,
  ) {
    mutablePortalEntries.value = PortalManager.Entries(
      entries = entries,
      disappearingEntries = emptyList(),
    )
    mutableBackstackEntries.value = backstack
  }

  fun startTransaction(backstack: PortalBackstack<KeyT>): Boolean {
    // reentrant
    if(transactionBuilder != null) return false

    transactionBuilder = PortalEntryBuilder(
      backstack = backstack,
      transactionPortalEntries = mutablePortalEntries.value.entries.toMutableList(),
      transactionDisappearingPortalEntries = mutablePortalEntries.value.disappearingEntries.toMutableList(),
      transactionBackstackEntries = mutableBackstackEntries.value.toMutableList(),
      backstackState = PortalBackstackState.None,
      validation = validation,
    )

    return true
  }

  fun <R> transact(
    builder: PortalEntryBuilder<KeyT>.() -> R,
  ) = requireNotNull(transactionBuilder) {
    "Cannot transact if not in a transaction"
  }.builder()

  fun rollbackTransaction() {
    transactionBuilder = null
  }

  fun commitTransaction() {
    transactionBuilder?.build()?.let { payload ->
      mutablePortalEntries.value = PortalManager.Entries(
        entries = payload.entries,
        disappearingEntries = payload.disappearingEntries,
      )
      mutableBackstackEntries.value = payload.backstackEntries
    }
    transactionBuilder?.postTransactionOps?.forEach { it() }
    transactionBuilder = null
  }
}
