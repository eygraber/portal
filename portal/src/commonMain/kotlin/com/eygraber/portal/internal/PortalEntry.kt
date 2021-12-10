package com.eygraber.portal.internal

import androidx.compose.runtime.Immutable
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalCompositionState
import com.eygraber.portal.PortalTransition

@Immutable
internal data class PortalEntry<PortalKey>(
  val key: PortalKey,
  val wasContentPreviouslyVisible: Boolean,
  val isDisappearing: Boolean,
  val isBackstackMutation: Boolean,
  val compositionState: PortalCompositionState,
  val transitionOverride: PortalTransition?,
  val portal: Portal
) {
  override fun toString() =
    """$name(
      |  key=$key,
      |  wasContentPreviouslyVisible=$wasContentPreviouslyVisible
      |  isDisappearing=$isDisappearing
      |  isBackstackMutation=$isBackstackMutation,
      |  compositionState=$compositionState
      |)""".trimMargin()

  private inline val name get() = this::class.simpleName
}

internal inline val <PortalKey> PortalEntry<PortalKey>.isAttachedToComposition
  get() = compositionState.isAddedOrAttached
