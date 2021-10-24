package com.eygraber.portal.kodein.di

import com.eygraber.portal.ChildPortal
import com.eygraber.portal.LifecyclePortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalRemovedListener
import kotlinx.atomicfu.atomic
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.provider

public interface KodeinDIPortalInitializer : KodeinDIComponentInitializer<LifecyclePortal> {
  override fun initializeKodeinDI(): DI = DI.lazy {
    extend(parentDI)

    bind<Portal>(overrides = kodeinDIComponent is ChildPortal) with provider {
      kodeinDIComponent
    }

    provideModule()?.let { module ->
      import(module, allowOverride = true)
    }
  }
}

public abstract class KodeinDIPortal : LifecyclePortal, KodeinDIPortalInitializer {
  override val parentDI: DI by closestDI()

  @Suppress("LeakingThis")
  final override val di: DI = initializeKodeinDI()

  private val listeners = atomic(emptyList<PortalRemovedListener>())

  override fun addPortalRemovedListener(listener: PortalRemovedListener) {
    listeners.value = listeners.value + listener
  }

  override fun removePortalRemovedListener(listener: PortalRemovedListener) {
    listeners.value = listeners.value - listener
  }

  override fun onPortalRemoved(isCompletelyRemoved: Boolean) {
    listeners.value.forEach { it.onPortalRemoved(isCompletelyRemoved) }
  }
}

/**
 * Provides a [KodeinDIPortal] that needs to be present at the top of the [Portal] hierarchy.
 *
 * All other [KodeinDIPortal] in the hierarchy should be children of that instance.
 *
 * @param [parentDI] the application level DI that will be extended throughout the [Portal] hierarchy.
 */
public abstract class KodeinRootPortal(
  final override val parentDI: DI
) : ParentPortal, KodeinDIPortal()

public interface KodeinDIComponentInitializer<T> : DIAware {
  public val parentDI: DI

  @Suppress("UNCHECKED_CAST")
  public val kodeinDIComponent: T
    get() = this as T

  public fun provideModule(): DI.Module? = null

  public fun initializeKodeinDI(): DI
}
