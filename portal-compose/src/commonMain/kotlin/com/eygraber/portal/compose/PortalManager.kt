package com.eygraber.portal.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.eygraber.portal.AbstractPortalManager
import com.eygraber.portal.PortalManagerValidation
import com.eygraber.portal.PortalRendererState
import com.eygraber.portal.internal.PortalEntry

@Suppress("MaxLineLength")
public class PortalManager<KeyT>(
  private val defaultTransitionsProvider: ComposePortalTransitionsProvider = ComposePortalTransitionsProvider.Default,
  defaultErrorHandler: ((Throwable) -> Unit)? = null,
  validation: PortalManagerValidation = PortalManagerValidation()
) : AbstractPortalManager<KeyT, ComposePortalEntry<KeyT>, ComposePortalEntry.EnterExtra, ComposePortalEntry.ExitExtra, ComposePortal>(
  defaultErrorHandler,
  createEntryCallbacks(),
  validation
) {
  @Composable
  public fun Render() {
    val portalEntries by portalEntriesUpdateFlow().collectAsState()

    for(entry in portalEntries) {
      PortalRenderer(entry)
    }
  }

  @Composable
  private fun PortalRenderer(
    entry: ComposePortalEntry<KeyT>
  ) {
    val transitionOverride = when(entry.rendererState) {
      PortalRendererState.Added,
      PortalRendererState.Attached -> entry.enterExtra?.transitionOverride

      PortalRendererState.Detached,
      PortalRendererState.Removed -> entry.exitExtra?.transitionOverride
    }

    val (enterTransition, exitTransition) = when(transitionOverride) {
      null -> when(val transitionProvider = entry.portal) {
        is ComposePortalTransitionsProvider -> transitionProvider.provideTransitions(
          compositionState = entry.rendererState,
          isForBackstack = entry.isBackstackMutation
        )

        else -> defaultTransitionsProvider.provideTransitions(
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
      with(entry.portal) {
        Render()
      }

      if(entry.isDisappearing) {
        DisposableEffect(Unit) {
          onDispose {
            withTransaction {
              makeEntryDisappear(entry.key)
            }
          }
        }
      }
    }
  }
}

private inline val <PortalKey> ComposePortalEntry<PortalKey>.isAttachedToComposition: Boolean
  get() = rendererState.isAddedOrAttached

@Suppress("MaxLineLength")
private fun <PortalKey> createEntryCallbacks() =
  object : PortalEntry.Callbacks<PortalKey, ComposePortalEntry<PortalKey>, ComposePortalEntry.EnterExtra, ComposePortalEntry.ExitExtra, ComposePortal> {
    override fun create(
      key: PortalKey,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      isBackstackMutation: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.EnterExtra?,
      portal: ComposePortal
    ) = ComposePortalEntry(
      key = key,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isDisappearing = isDisappearing,
      isBackstackMutation = isBackstackMutation,
      rendererState = rendererState,
      enterExtra = extra,
      exitExtra = null,
      portal = portal
    )

    override fun attach(
      entry: ComposePortalEntry<PortalKey>,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.EnterExtra?
    ) = entry.copy(
      isBackstackMutation = isBackstackMutation,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      rendererState = rendererState,
      enterExtra = extra,
      exitExtra = null
    )

    override fun detach(
      entry: ComposePortalEntry<PortalKey>,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.ExitExtra?
    ) = entry.copy(
      isBackstackMutation = isBackstackMutation,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      rendererState = rendererState,
      enterExtra = null,
      exitExtra = extra
    )

    override fun remove(
      entry: ComposePortalEntry<PortalKey>,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.ExitExtra?
    ) = entry.copy(
      isBackstackMutation = isBackstackMutation,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isDisappearing = isDisappearing,
      rendererState = rendererState,
      enterExtra = null,
      exitExtra = extra
    )
  }
