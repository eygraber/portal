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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import com.eygraber.portal.DisappearingPortalEntry
import com.eygraber.portal.PortalEntry
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
  private val composePortalEntriesUpdateFlow =
    portalEntriesUpdateFlow().map { (entries, disappearingEntries) ->
      combineEntriesAndDisappearingEntries(
        entries = entries,
        disappearingEntries = disappearingEntries
      )
    }

  private val initial by lazy {
    portalEntriesUpdateFlow().value.let { (entries, disappearingEntries) ->
      combineEntriesAndDisappearingEntries(
        entries = entries,
        disappearingEntries = disappearingEntries
      )
    }
  }

  @Composable
  public fun Render() {
    val portalEntries by composePortalEntriesUpdateFlow.collectAsState(initial = initial)

    for(entry in portalEntries) {
      // the key function requires uniqueness at this point in composition
      // since there could be cases where there are multiple PortalEntry keys that are equal
      // we also use the PortalEntry uid which is guaranteed to be unique for each portal that is added
      key(entry.uid to entry.key) {
        PortalRenderer(entry)
      }
    }
  }

  @Composable
  private fun PortalRenderer(entry: ComposePortalEntry<out KeyT>) {
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
          backstackState = entry.backstackState
        )

        else -> defaultTransitionProvider.provideTransitions(
          compositionState = entry.rendererState,
          backstackState = entry.backstackState
        )
      }

      else -> transitionOverride
    }

    val wasContentPreviouslyVisible =
      entry.isDisappearing && entry.isAttachedToComposition ||
        entry.wasContentPreviouslyVisible

    val isContentVisible = !entry.isDisappearing && entry.isAttachedToComposition
    val visibleState = remember(
      entry.isDisappearing,
      isContentVisible,
      wasContentPreviouslyVisible
    ) {
      MutableTransitionState(wasContentPreviouslyVisible)
        .apply { targetState = isContentVisible }
    }

    // since this case won't render the AnimatedVisibility content
    // we need to handle disposing the disappearing entry here
    if(entry.isDisappearing && !entry.isAttachedToComposition && !entry.wasContentPreviouslyVisible) {
      RegisterDisposeForNonAnimatedDisappearance(entry)
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
              makeEntryDisappear(entry.uid)
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

  @Composable
  private fun RegisterDisposeForNonAnimatedDisappearance(entry: ComposePortalEntry<out KeyT>) {
    var keepInComposition by remember { mutableStateOf(true) }

    if(keepInComposition) {
      DisposableEffect(Unit) {
        onDispose {
          withTransaction {
            makeEntryDisappear(entry.uid)
          }
        }
      }
      Snapshot.withMutableSnapshot {
        keepInComposition = false
      }
    }
  }
}

private inline val <PortalKey> ComposePortalEntry<PortalKey>.isAttachedToComposition: Boolean
  get() = rendererState.isAddedOrAttached

@Suppress("NOTHING_TO_INLINE")
private inline fun <KeyT> combineEntriesAndDisappearingEntries(
  entries: List<PortalEntry<KeyT>>,
  disappearingEntries: List<DisappearingPortalEntry<out KeyT>>
) = buildList {
  if(entries.isEmpty()) {
    for(i in disappearingEntries.indices) {
      add(
        ComposePortalEntry.fromPortalEntry(
          entry = disappearingEntries[i].entry,
          isDisappearing = true
        )
      )
    }
  }
  else {
    var disappearingCursor = 0
    for(i in entries.indices) {
      var hasRemainingDisappearingEntries = disappearingCursor <= disappearingEntries.lastIndex
      while(hasRemainingDisappearingEntries && disappearingEntries[disappearingCursor].index == i) {
        add(
          ComposePortalEntry.fromPortalEntry(
            entry = disappearingEntries[disappearingCursor].entry,
            isDisappearing = true
          )
        )
        hasRemainingDisappearingEntries = ++disappearingCursor <= disappearingEntries.lastIndex
      }

      add(
        ComposePortalEntry.fromPortalEntry(
          entry = entries[i],
          isDisappearing = false
        )
      )
    }

    if(disappearingCursor <= disappearingEntries.lastIndex) {
      for(i in disappearingCursor..disappearingEntries.lastIndex) {
        add(
          ComposePortalEntry.fromPortalEntry(
            entry = disappearingEntries[i].entry,
            isDisappearing = true
          )
        )
      }
    }
  }
}
