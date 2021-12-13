package com.eygraber.portal

public interface Portal

public interface ParentPortal : Portal {
  public val portalManagers: List<AbstractPortalManager<*, *, *, *>>
}

public interface ChildPortal : Portal {
  public val parent: Portal
}

public interface SaveablePortal {
  public fun saveState()
}
