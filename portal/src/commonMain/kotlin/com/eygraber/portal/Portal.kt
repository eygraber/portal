package com.eygraber.portal

import androidx.compose.runtime.Composable

public fun interface Portal {
  @Composable
  public fun render()
}

public interface ParentPortal : Portal {
  public val portalManagers: List<PortalManager<*>>
}

public interface ChildPortal : Portal {
  public val parent: Portal
}
