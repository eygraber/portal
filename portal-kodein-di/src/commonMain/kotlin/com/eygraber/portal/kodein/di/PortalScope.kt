package com.eygraber.portal.kodein.di

import com.eygraber.portal.LifecyclePortal
import com.eygraber.portal.PortalRemovedListener
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindings.BindingDI
import org.kodein.di.bindings.Multiton
import org.kodein.di.bindings.NoArgBindingDI
import org.kodein.di.bindings.Scope
import org.kodein.di.bindings.ScopeRegistry
import org.kodein.di.bindings.Singleton
import org.kodein.di.bindings.StandardScopeRegistry
import org.kodein.di.internal.synchronizedIfNull
import org.kodein.di.multiton
import org.kodein.di.scoped
import org.kodein.di.singleton

@PublishedApi
internal object PortalScope : Scope<LifecyclePortal> {
  private val newRegistry = ::StandardScopeRegistry
  private val map = HashMap<LifecyclePortal, ScopeRegistry>()

  override fun getRegistry(context: LifecyclePortal) = synchronizedIfNull(
    lock = map,
    predicate = { map[context] },
    ifNotNull = { it },
    ifNull = {
      newRegistry().also { registry ->
        map[context] = registry

        val listener = object : PortalRemovedListener {
          override fun onPortalRemoved(isCompletelyRemoved: Boolean) {
            if(isCompletelyRemoved) {
              context.removePortalRemovedListener(this)
              registry.clear()
              map.remove(context)
            }
          }
        }

        context.addPortalRemovedListener(listener)
      }
    }
  )
}

public inline fun <reified T : Any> DI.Builder.portalSingleton(
  noinline creator: NoArgBindingDI<LifecyclePortal>.() -> T
): Singleton<LifecyclePortal, T> = scoped(PortalScope).singleton(creator = creator)

public inline fun <reified A : Any, reified T : Any> DI.Builder.portalMultiton(
  noinline creator: BindingDI<LifecyclePortal>.(A) -> T
): Multiton<LifecyclePortal, A, T> = scoped(PortalScope).multiton(creator = creator)

public inline fun <reified T : Any> DI.Builder.bindPortalSingleton(
  tag: Any? = null,
  overrides: Boolean? = null,
  noinline creator: NoArgBindingDI<LifecyclePortal>.() -> T
) {
  bind<T>(
    tag = tag,
    overrides = overrides
  ) with portalSingleton(creator)
}

public inline fun <reified A : Any, reified T : Any> DI.Builder.bindPortalMultiton(
  tag: Any? = null,
  overrides: Boolean? = null,
  noinline creator: BindingDI<LifecyclePortal>.(A) -> T
) {
  bind<T>(
    tag = tag,
    overrides = overrides
  ) with portalMultiton(creator)
}
