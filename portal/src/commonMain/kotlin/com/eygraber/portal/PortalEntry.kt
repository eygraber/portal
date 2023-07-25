package com.eygraber.portal

import kotlin.jvm.JvmInline

public data class PortalEntry<KeyT>(
  public val portal: KeyedPortal<out KeyT>,
  public val wasContentPreviouslyVisible: Boolean,
  public val backstackState: PortalBackstackState,
  public val rendererState: PortalRendererState,
  public val enterTransitionOverride: EnterTransitionOverride?,
  public val exitTransitionOverride: ExitTransitionOverride?,
  public val uid: Id
) {
  public val key: KeyT = portal.key

  @JvmInline
  public value class Id(public val id: Int)

  override fun toString(): String =
    """$name(
      |  key=$key,
      |  wasContentPreviouslyVisible=$wasContentPreviouslyVisible,
      |  backstackState=$backstackState,
      |  rendererState=$rendererState,
      |  enterTransitionOverride=$enterTransitionOverride,
      |  exitTransitionOverride=$exitTransitionOverride,
      |  uid = $uid
      |)
    """.trimMargin()

  private inline val name get() = this::class.simpleName
}
