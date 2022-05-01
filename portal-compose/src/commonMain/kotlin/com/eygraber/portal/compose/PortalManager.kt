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

public class PortalManager<KeyT>(
  private val defaultTransitionsProvider: ComposePortalTransitionsProvider = ComposePortalTransitionsProvider.Default,
  defaultErrorHandler: ((Throwable) -> Unit)? = null,
  validation: PortalManagerValidation = PortalManagerValidation()
) : AbstractPortalManager<KeyT, ComposePortalEntry<KeyT>, ComposePortalEntry.Extra, ComposePortal>(
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
    val (enterTransition, exitTransition) = when(val override = entry.extra?.transitionOverride) {
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

private fun <PortalKey> createEntryCallbacks() =
  object : PortalEntry.Callbacks<PortalKey, ComposePortalEntry<PortalKey>, ComposePortalEntry.Extra, ComposePortal> {
    override fun create(
      key: PortalKey,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      isBackstackMutation: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.Extra?,
      portal: ComposePortal
    ) = ComposePortalEntry(
      key = key,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isDisappearing = isDisappearing,
      isBackstackMutation = isBackstackMutation,
      rendererState = rendererState,
      extra = extra,
      portal = portal
    )

    override fun attach(
      entry: ComposePortalEntry<PortalKey>,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.Extra?
    ) = entry.copy(
      isBackstackMutation = isBackstackMutation,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      rendererState = rendererState,
      extra = extra
    )

    override fun detach(
      entry: ComposePortalEntry<PortalKey>,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.Extra?
    ) = entry.copy(
      isBackstackMutation = isBackstackMutation,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      rendererState = rendererState,
      extra = extra
    )

    override fun remove(
      entry: ComposePortalEntry<PortalKey>,
      isBackstackMutation: Boolean,
      wasContentPreviouslyVisible: Boolean,
      isDisappearing: Boolean,
      rendererState: PortalRendererState,
      extra: ComposePortalEntry.Extra?
    ) = entry.copy(
      isBackstackMutation = isBackstackMutation,
      wasContentPreviouslyVisible = wasContentPreviouslyVisible,
      isDisappearing = isDisappearing,
      rendererState = rendererState,
      extra = extra
    )
  }
