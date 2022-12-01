package com.eygraber.portal.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalManagerValidation
import com.eygraber.portal.PortalRendererState
import kotlinx.coroutines.flow.map

public class ComposePortalManager<KeyT>(
  private val defaultTransitionProvider: PortalTransitionProvider = PortalTransitionProvider.Default,
  defaultErrorHandler: ((Throwable) -> Unit)? = null,
  validation: PortalManagerValidation = PortalManagerValidation()
) : PortalManager<KeyT>(
  defaultErrorHandler,
  validation
) {
  private val composePortalEntriesUpdateFlow = portalEntriesUpdateFlow().map { entries ->
    entries.map(ComposePortalEntry.Companion::fromPortalEntry)
  }

  @Composable
  public fun Render() {
    val portalEntries by composePortalEntriesUpdateFlow.collectAsState(
      initial = portalEntriesUpdateFlow().value.map(ComposePortalEntry.Companion::fromPortalEntry)
    )

    for(entry in portalEntries) {
      PortalRenderer(entry)
    }
  }

  @Composable
  private fun PortalRenderer(entry: ComposePortalEntry<KeyT>) {
    val transitionOverride = when(entry.rendererState) {
      PortalRendererState.Added,
      PortalRendererState.Attached -> entry.enterTransitionOverride?.let { enterTransition ->
        PortalTransition(
          enterTransition,
          ExitTransition.None
        )
      }

      PortalRendererState.Detached,
      PortalRendererState.Removed -> entry.exitTransitionOverride?.let { exitTransition ->
        PortalTransition(
          EnterTransition.None,
          exitTransition
        )
      }
    }

    val (enterTransition, exitTransition) = when(transitionOverride) {
      null -> when(val transitionProvider = entry.portal) {
        is PortalTransitionProvider -> transitionProvider.provideTransitions(
          compositionState = entry.rendererState,
          isForBackstack = entry.isBackstackMutation
        )

        else -> defaultTransitionProvider.provideTransitions(
          compositionState = entry.rendererState,
          isForBackstack = entry.isBackstackMutation
        )
      }

      else -> transitionOverride
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
            makeEntryDisappear(entry.key)
          }
        }
      }
    }

    AnimatedVisibility(
      visibleState = visibleState,
      enter = enterTransition,
      exit = exitTransition
    ) {
      entry.portal.Render()

      if(entry.isDisappearing) {
        DisposableEffect(Unit) {
          onDispose {
            withTransaction {
              makeEntryDisappear(entry.key)
            }
          }
        }
      }
      else if(entry.rendererState == PortalRendererState.Added && visibleState.isIdle) {
        LaunchedEffect(Unit) {
          withTransaction {
            markAddedEntryAsAttached(entry.key)
          }
        }
      }
    }
  }
}

private inline val <PortalKey> ComposePortalEntry<PortalKey>.isAttachedToComposition: Boolean
  get() = rendererState.isAddedOrAttached
