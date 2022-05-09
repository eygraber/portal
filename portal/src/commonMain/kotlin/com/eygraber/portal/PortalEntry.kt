package com.eygraber.portal

public data class PortalEntry<KeyT>(
  public val key: KeyT,
  public val wasContentPreviouslyVisible: Boolean,
  public val isDisappearing: Boolean,
  public val isBackstackMutation: Boolean,
  public val rendererState: PortalRendererState,
  public val enterTransitionOverride: EnterTransitionOverride?,
  public val exitTransitionOverride: ExitTransitionOverride?,
  public val portal: Portal
) {
  override fun toString(): String =
    """$name(
      |  key=$key,
      |  wasContentPreviouslyVisible=$wasContentPreviouslyVisible
      |  isDisappearing=$isDisappearing
      |  isBackstackMutation=$isBackstackMutation,
      |  rendererState=$rendererState,
      |  enterTransitionOverride=$enterTransitionOverride,
      |  exitTransitionOverride=$exitTransitionOverride
      |)
    """.trimMargin()

  private inline val name get() = this::class.simpleName
}
