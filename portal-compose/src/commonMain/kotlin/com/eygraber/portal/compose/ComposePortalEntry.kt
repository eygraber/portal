package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
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
  override val enterExtra: EnterExtra?,
  override val exitExtra: ExitExtra?,
  override val portal: ComposePortal
) : PortalEntry<KeyT, ComposePortalEntry.EnterExtra, ComposePortalEntry.ExitExtra, ComposePortal> {
  override fun toString(): String =
    """$name(
      |  key=$key,
      |  wasContentPreviouslyVisible=$wasContentPreviouslyVisible
      |  isDisappearing=$isDisappearing
      |  isBackstackMutation=$isBackstackMutation,
      |  rendererState=$rendererState,
      |  extra=$enterExtra
      |)
    """.trimMargin()

  private inline val name get() = this::class.simpleName

  public data class EnterExtra(
    val transitionOverride: ComposePortalTransition?
  ) : PortalEntry.Extra.Enter {
    public companion object {
      public fun enterTransitionOverride(
        enterTransition: EnterTransition
      ): EnterExtra = EnterExtra(ComposePortalTransition.enter(enterTransition))
    }
  }

  public data class ExitExtra(
    val transitionOverride: ComposePortalTransition?
  ) : PortalEntry.Extra.Exit {
    public companion object {
      public fun exitTransitionOverride(
        exitTransition: ExitTransition
      ): ExitExtra = ExitExtra(ComposePortalTransition.exit(exitTransition))
    }
  }
}
