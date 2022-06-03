package com.eygraber.portal

public interface PortalRemovedListener {
  public fun onPortalRemoved(isCompletelyRemoved: Boolean)
}

public interface PortalLifecycleManager : PortalRemovedListener {
  public fun addPortalRemovedListener(listener: PortalRemovedListener)
  public fun removePortalRemovedListener(listener: PortalRemovedListener)
}
