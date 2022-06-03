package com.eygraber.portal.kodein.di

import com.eygraber.portal.ChildPortal
import com.eygraber.portal.PortalLifecycleManager
import org.kodein.di.DI
import org.kodein.di.DIAware
import kotlin.reflect.KProperty

public interface DIPropertyDelegateProvider<in T> {
  public operator fun provideDelegate(thisRef: T, property: KProperty<*>?): Lazy<DI>
}

private class PortalDIPropertyDelegateProvider : DIPropertyDelegateProvider<PortalLifecycleManager> {
  override operator fun provideDelegate(thisRef: PortalLifecycleManager, property: KProperty<*>?) =
    lazy { closestDI(thisRef, thisRef) }
}

private tailrec fun closestDI(thisRef: Any?, portal: Any?): DI = when {
  portal == null -> error(
    "Trying to find closest DI, but no portals in the hierarchy implement DIAware"
  )
  portal is KodeinDIRoot -> portal.di
  portal != thisRef && portal is DIAware -> portal.di
  else -> closestDI(thisRef, (portal as? ChildPortal)?.parent)
}

public fun closestDI(): DIPropertyDelegateProvider<PortalLifecycleManager> = PortalDIPropertyDelegateProvider()
