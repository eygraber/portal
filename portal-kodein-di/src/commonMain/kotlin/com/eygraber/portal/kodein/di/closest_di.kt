package com.eygraber.portal.kodein.di

import com.eygraber.portal.ChildPortal
import com.eygraber.portal.LifecyclePortal
import com.eygraber.portal.Portal
import org.kodein.di.DI
import org.kodein.di.DIAware
import kotlin.reflect.KProperty

public interface DIPropertyDelegateProvider<in T> {
  public operator fun provideDelegate(thisRef: T, property: KProperty<*>?): Lazy<DI>
}

private class PortalDIPropertyDelegateProvider : DIPropertyDelegateProvider<LifecyclePortal> {
  override operator fun provideDelegate(thisRef: LifecyclePortal, property: KProperty<*>?) =
    lazy { closestDI(thisRef, thisRef) }
}

private tailrec fun closestDI(thisRef: Any?, portal: Portal?): DI = when {
  portal == null -> error(
    "Trying to find closest DI, but no DI container was found at all. Your top level Portal should be a PortalRoot."
  )
  portal is KodeinRootPortal -> portal.di
  portal != thisRef && portal is DIAware -> portal.di
  else -> closestDI(thisRef, (portal as? ChildPortal)?.parent)
}

public fun closestDI(): DIPropertyDelegateProvider<LifecyclePortal> = PortalDIPropertyDelegateProvider()
