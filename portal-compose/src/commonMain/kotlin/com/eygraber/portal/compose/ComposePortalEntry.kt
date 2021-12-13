package com.eygraber.portal.compose

import androidx.compose.runtime.Immutable
import com.eygraber.portal.PortalRendererState
import com.eygraber.portal.internal.PortalEntry

@Immutable
public data class ComposePortalEntry<KeyT>(
  override val key: KeyT,
  override val wasContentPreviouslyVisible: Boolean,
  override val isDisappearing: Boolean,
  override val isBackstackMutation: Boolean,
  override val rendererState: PortalRendererState,
  override val extra: Extra?,
  override val portal: ComposePortal
) : PortalEntry<KeyT, ComposePortalEntry.Extra, ComposePortal> {
  override fun toString(): String =
    """$name(
      |  key=$key,
      |  wasContentPreviouslyVisible=$wasContentPreviouslyVisible
      |  isDisappearing=$isDisappearing
      |  isBackstackMutation=$isBackstackMutation,
      |  rendererState=$rendererState,
      |  extra=$extra
      |)""".trimMargin()

  private inline val name get() = this::class.simpleName

  public data class Extra(
    val transitionOverride: ComposePortalTransition?
  ) : PortalEntry.Extra
}
