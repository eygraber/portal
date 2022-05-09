package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Immutable
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalRendererState

@Immutable
internal data class ComposePortalEntry<KeyT>(
  val key: KeyT,
  val wasContentPreviouslyVisible: Boolean,
  val isDisappearing: Boolean,
  val isBackstackMutation: Boolean,
  val rendererState: PortalRendererState,
  val enterTransitionOverride: EnterTransition?,
  val exitTransitionOverride: ExitTransition?,
  val portal: ComposePortal
) {
  companion object {
    fun <KeyT> fromPortalEntry(entry: PortalEntry<KeyT>) = ComposePortalEntry(
      key = entry.key,
      wasContentPreviouslyVisible = entry.wasContentPreviouslyVisible,
      isDisappearing = entry.isDisappearing,
      isBackstackMutation = entry.isBackstackMutation,
      rendererState = entry.rendererState,
      enterTransitionOverride = entry.enterTransitionOverride?.toComposeTransition(),
      exitTransitionOverride = entry.exitTransitionOverride?.toComposeTransition(),
      portal = entry.portal as? ComposePortal ?: error("portal must be a ComposePortal")
    )
  }
}
