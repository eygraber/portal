package com.eygraber.portal

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.eygraber.portal.internal.PortalEntry
import com.eygraber.portal.internal.PortalState
import com.eygraber.portal.internal.deserializePortalManagerState
import com.eygraber.portal.internal.isAttachedToComposition
import com.eygraber.portal.internal.serializePortalManagerState
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

public interface PortalManagerQueries<PortalKey> {
  public val size: Int

  public val portals: List<Pair<PortalKey, Portal>>

  public operator fun contains(key: PortalKey): Boolean
}

@DslMarker
internal annotation class PortalTransactionBuilderDsl

public class PortalManager<PortalKey>(
  private val defaultTransitionsProvider: PortalTransitionsProvider = PortalTransitionsProvider.Default,
  private val defaultErrorHandler: ((Throwable) -> Unit)? = null,
  validation: PortalManagerValidation = PortalManagerValidation()
) : PortalManagerQueries<PortalKey> {
  override val size: Int get() = portalState.portalEntries.filterNot { it.isDisappearing }.size

  override val portals: List<Pair<PortalKey, Portal>> get() = portalState.portalEntries.map { it.key to it.portal }

  override operator fun contains(key: PortalKey): Boolean =
    portalState.portalEntries.findLast { entry ->
      entry.key == key
    } != null

  public fun saveState(
    portalKeySerializer: (PortalKey) -> String
  ): String = lock.withLock {
    serializePortalManagerState(portalKeySerializer, portalState)
  }

  public fun restoreState(
    serializedState: String,
    portalKeyDeserializer: (String) -> PortalKey,
    portalFactory: (PortalKey) -> Portal
  ) {
    lock.withLock {
      val (entries, backstack) = deserializePortalManagerState(
        serializedState = serializedState,
        portalKeyDeserializer = portalKeyDeserializer,
        portalFactory = portalFactory
      )

      portalState.restoreState(
        entries = entries,
        backstack = backstack
      )
    }
  }

  public fun withTransaction(
    errorHandler: ((Throwable) -> Unit)? = defaultErrorHandler,
    @PortalTransactionBuilderDsl builder: EntryBuilder<PortalKey>.() -> Unit
  ) {
    lock.withLock {
      portalState.startTransaction(_backstack)

      try {
        portalState.transact(builder)
        portalState.commitTransaction()
      }
      catch(error: Throwable) {
        errorHandler?.invoke(error)
        portalState.rollbackTransaction()
      }
    }
  }

  public fun updates(): Flow<Unit> = portalState.portalEntriesUpdateFlow().map {}

  @Composable
  public fun render() {
    val portalEntries by portalState.portalEntriesUpdateFlow().collectAsState()

    for(entry in portalEntries) {
      PortalRenderer(entry)
    }
  }

  @Composable
  private fun PortalRenderer(
    entry: PortalEntry<PortalKey>
  ) {
    val (enterTransition, exitTransition) = when(val override = entry.transitionOverride) {
      null -> when(val transitionProvider = entry.portal) {
        is PortalTransitionsProvider -> transitionProvider.provideTransitions(
          compositionState = entry.compositionState,
          isForBackstack = entry.isBackstackMutation
        )

        else -> defaultTransitionsProvider.provideTransitions(
          compositionState = entry.compositionState,
          isForBackstack = entry.isBackstackMutation
        )
      }

      else -> override
    }

    val wasContentPreviouslyVisible =
      entry.isDisappearing && entry.isAttachedToComposition ||
        entry.wasContentPreviouslyVisible

    val isContentVisible = !entry.isDisappearing && entry.isAttachedToComposition
    val visibleState = remember(
      entry.isDisappearing, isContentVisible, wasContentPreviouslyVisible
    ) {
      MutableTransitionState(wasContentPreviouslyVisible)
        .apply { targetState = isContentVisible }
    }

    // since this case won't render the AnimatedVisibility content
    // we need to handle disposing the disappearing entry here
    if(entry.isDisappearing && !entry.isAttachedToComposition) {
      DisposableEffect(Unit) {
        onDispose {
          withTransaction {
            portalState.transact {
              disappear(entry.key)
            }
          }
        }
      }
    }

    AnimatedVisibility(
      visibleState = visibleState,
      enter = enterTransition,
      exit = exitTransition
    ) {
      with(entry.portal) {
        render()
      }

      if(entry.isDisappearing) {
        DisposableEffect(Unit) {
          onDispose {
            withTransaction {
              portalState.transact {
                disappear(entry.key)
              }
            }
          }
        }
      }
    }
  }

  private val lock = reentrantLock()
  private val portalState = PortalState<PortalKey>(
    validation = validation
  )

  private val _backstack: PortalBackstack<PortalKey> = PortalBackstackImpl(portalState)

  public val backstack: ReadOnlyBackstack = _backstack

  public interface EntryBuilder<PortalKey> : PortalManagerQueries<PortalKey> {
    public val backstack: PortalBackstack<PortalKey>

    public fun add(
      key: PortalKey,
      isAttachedToComposition: Boolean = true,
      transitionOverride: EnterTransition? = null,
      portal: Portal,
    )

    public fun attachToComposition(
      key: PortalKey,
      transitionOverride: EnterTransition? = null
    )

    public fun detachFromComposition(
      key: PortalKey,
      transitionOverride: ExitTransition? = null
    )

    public fun remove(
      key: PortalKey,
      transitionOverride: ExitTransition? = null
    )

    public fun clear(
      transitionsOverrideProvider: ((PortalKey) -> ExitTransition?)? = null
    )
  }
}
