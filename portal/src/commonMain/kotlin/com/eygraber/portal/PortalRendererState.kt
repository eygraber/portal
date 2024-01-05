package com.eygraber.portal

public enum class PortalRendererState {
  Added,
  Attached,
  Detached,
  Removed,
  ;

  public val isAddedOrAttached: Boolean get() = this == Added || this == Attached
  public val isAddedOrRemoved: Boolean get() = this == Added || this == Removed
}
