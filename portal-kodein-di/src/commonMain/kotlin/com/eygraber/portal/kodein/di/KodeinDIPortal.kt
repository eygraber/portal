package com.eygraber.portal.kodein.di

import com.eygraber.portal.ChildPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.PortalLifecycleManager
import com.eygraber.portal.PortalRemovedListener
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bind
import org.kodein.di.instance

public interface KodeinDIPortalInitializer : KodeinDIComponentInitializer<PortalLifecycleManager> {
  override fun initializeKodeinDI(): DI = DI.lazy {
    extend(parentDI)

    bind<Portal>(overrides = kodeinDIComponent is ChildPortal) with instance(kodeinDIComponent as Portal)

    if(kodeinDIComponent is ChildPortal) {
      val parent = (kodeinDIComponent as ChildPortal).parent
      bind<ParentPortal>(overrides = parent is ChildPortal) with instance(parent)
    }

    provideModule()?.let { module ->
      import(module, allowOverride = true)
    }
  }
}

@Suppress("AbstractClassCanBeConcreteClass")
public abstract class KodeinDIPortal : PortalLifecycleManager, KodeinDIPortalInitializer {
  override val parentDI: DI by closestDI()

  @Suppress("LeakingThis")
  final override val di: DI = initializeKodeinDI()

  private val listeners = atomic(emptyList<PortalRemovedListener>())

  override fun addPortalRemovedListener(listener: PortalRemovedListener) {
    listeners.update { oldListeners -> oldListeners + listener }
  }

  override fun removePortalRemovedListener(listener: PortalRemovedListener) {
    listeners.update { oldListeners -> oldListeners - listener }
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
public abstract class KodeinDIRoot(
  final override val parentDI: DI,
) : ParentPortal, KodeinDIPortal()

public interface KodeinDIComponentInitializer<T> : DIAware {
  public val parentDI: DI

  @Suppress("UNCHECKED_CAST")
  public val kodeinDIComponent: T
    get() = this as T

  public fun provideModule(): DI.Module? = null

  public fun initializeKodeinDI(): DI
}
