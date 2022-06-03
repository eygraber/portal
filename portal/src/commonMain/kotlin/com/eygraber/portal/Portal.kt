package com.eygraber.portal

public interface Portal

public interface KeyedPortal<KeyT> : Portal {
  public val key: KeyT
}

public interface ParentPortal {
  public val portalManagers: List<PortalManager<*>>
}

public interface ChildPortal {
  public val parent: ParentPortal
}

public interface SaveablePortal {
  public fun saveState()
}
