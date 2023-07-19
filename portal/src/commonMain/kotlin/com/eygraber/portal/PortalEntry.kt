package com.eygraber.portal

public data class PortalEntry<KeyT>(
  public val portal: KeyedPortal<out KeyT>,
  public val wasContentPreviouslyVisible: Boolean,
  public val isDisappearing: Boolean,
  public val backstackState: PortalBackstackState,
  public val rendererState: PortalRendererState,
  public val enterTransitionOverride: EnterTransitionOverride?,
  public val exitTransitionOverride: ExitTransitionOverride?,
  public val uid: Int
) {
  public val key: KeyT = portal.key

  override fun toString(): String =
    """$name(
      |  key=$key,
      |  wasContentPreviouslyVisible=$wasContentPreviouslyVisible
      |  isDisappearing=$isDisappearing
      |  backstackState=$backstackState,
      |  rendererState=$rendererState,
      |  enterTransitionOverride=$enterTransitionOverride,
      |  exitTransitionOverride=$exitTransitionOverride,
      |  uid = $uid
      |)
    """.trimMargin()

  private inline val name get() = this::class.simpleName
}
