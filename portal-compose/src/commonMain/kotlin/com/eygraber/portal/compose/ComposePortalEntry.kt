package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Immutable
import com.eygraber.portal.PortalBackstackState
import com.eygraber.portal.PortalEntry
import com.eygraber.portal.PortalRendererState

@Immutable
internal data class ComposePortalEntry<KeyT>(
  val portal: ComposePortal<KeyT>,
  val wasContentPreviouslyVisible: Boolean,
  val isDisappearing: Boolean,
  val backstackState: PortalBackstackState,
  val rendererState: PortalRendererState,
  val enterTransitionOverride: EnterTransition?,
  val exitTransitionOverride: ExitTransition?,
  val uid: PortalEntry.Id,
) {
  val key: KeyT = portal.key

  companion object {
    fun <KeyT> fromPortalEntry(
      entry: PortalEntry<KeyT>,
      isDisappearing: Boolean,
    ) = ComposePortalEntry(
      portal = entry.portal as? ComposePortal ?: error("portal must be a ComposePortal"),
      wasContentPreviouslyVisible = entry.wasContentPreviouslyVisible,
      isDisappearing = isDisappearing,
      backstackState = entry.backstackState,
      rendererState = entry.rendererState,
      enterTransitionOverride = entry.enterTransitionOverride?.toComposeTransition(),
      exitTransitionOverride = entry.exitTransitionOverride?.toComposeTransition(),
      uid = entry.uid,
    )
  }
}
