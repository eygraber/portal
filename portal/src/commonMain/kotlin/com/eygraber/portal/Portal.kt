package com.eygraber.portal

public interface Portal

public interface KeyedPortal<KeyT> : Portal {
  public val key: KeyT
}

public interface ParentPortal : Portal {
  public val portalManagers: List<PortalManager<*>>
}

public interface ChildPortal : Portal {
  public val parent: Portal
}

public interface SaveablePortal {
  public fun saveState()
}
