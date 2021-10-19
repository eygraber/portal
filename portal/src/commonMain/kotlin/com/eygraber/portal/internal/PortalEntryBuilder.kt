package com.eygraber.portal.internal

import com.eygraber.portal.Portal
import com.eygraber.portal.PortalBackstack
import com.eygraber.portal.PortalTransactionBuilderDsl
import com.eygraber.portal.PortalTransitions
import com.eygraber.portal.PortalTransitionsProvider
import com.eygraber.portal.Portals

internal class PortalEntryBuilder<PortalKey>(
  override val backstack: PortalBackstack<PortalKey>,
  private val transactionPortalEntries: MutableList<PortalEntry<PortalKey>>,
  private val transactionBackstackEntries: MutableList<PortalBackstackEntry<PortalKey>>,
  private val isForBackstack: Boolean,
  private val defaultTransitions: PortalTransitions,
  private val doesIncompatibleStateThrow: Boolean
) : Portals.EntryBuilder<PortalKey> {
  override val size get() = transactionPortalEntries.filterNot { it.isDisappearing }.size

  override fun contains(key: PortalKey) =
    transactionPortalEntries.findLast { entry ->
      entry.key == key
    } != null

  override fun add(
    key: PortalKey,
    isAttachedToComposition: Boolean,
    transitionsOverride: PortalTransitions?,
    portal: Portal
  ) {
    transactionPortalEntries += PortalEntry(
      key = key,
      wasContentPreviouslyVisible = false,
      isAttachedToComposition = isAttachedToComposition,
      isDisappearing = false,
      isBackstackMutation = false,
      transitions = transitionsOverride ?: defaultTransitions,
      portal = portal
    )
  }

  override fun attachToComposition(
    key: PortalKey,
    transitionsOverride: PortalTransitions?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          doesIncompatibleStateThrow -> error("Cannot attach if the entry is disappearing")
          else -> entry
        }

        else -> entry.copy(
          wasContentPreviouslyVisible = entry.isAttachedToComposition,
          isAttachedToComposition = true,
          isBackstackMutation = isForBackstack,
          transitions = transitionsOverride ?: entry.transitions
        )
      }
    }
  }

  override fun detachFromComposition(
    key: PortalKey,
    transitionsOverride: PortalTransitions?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          doesIncompatibleStateThrow -> error("Cannot detach if the entry is disappearing")
          else -> entry
        }
        else -> entry.copy(
          wasContentPreviouslyVisible = entry.isAttachedToComposition,
          isAttachedToComposition = false,
          isBackstackMutation = isForBackstack,
          transitions = transitionsOverride ?: entry.transitions
        )
      }
    }
  }

  override fun remove(
    key: PortalKey,
    transitionsOverride: PortalTransitions?
  ) {
    key.applyMutationToPortalEntries { entry ->
      when {
        entry.isDisappearing -> when {
          doesIncompatibleStateThrow -> error("Cannot remove if the entry is disappearing")
          else -> null
        }
        else -> entry.copy(
          wasContentPreviouslyVisible = entry.isAttachedToComposition,
          isAttachedToComposition = entry.isAttachedToComposition,
          isBackstackMutation = false,
          isDisappearing = true,
          transitions = transitionsOverride ?: entry.transitions
        )
      }
    }
  }

  override fun clear(
    transitionsOverrideProvider: PortalTransitionsProvider<PortalKey>
  ) {
    transactionPortalEntries.reversed().forEach { entry ->
      remove(
        key = entry.key,
        transitionsOverride = transitionsOverrideProvider?.invoke(entry.key) ?: entry.transitions
      )
    }
  }

  internal fun disappear(
    key: PortalKey
  ) {
    key.applyMutationToPortalEntries { entry ->
      if(entry.isDisappearing) {
        // removes the entry from portalEntries
        null
      }
      else {
        when {
          doesIncompatibleStateThrow -> error("Cannot disappear if the entry isn't disappearing")
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
    defaultTransitions = defaultTransitions,
    doesIncompatibleStateThrow = doesIncompatibleStateThrow
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
}
