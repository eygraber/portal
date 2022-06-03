package com.eygraber.portal.kodein.di

import com.eygraber.portal.PortalLifecycleManager
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
internal object PortalScope : Scope<PortalLifecycleManager> {
  private val newRegistry = ::StandardScopeRegistry
  private val map = HashMap<PortalLifecycleManager, ScopeRegistry>()

  override fun getRegistry(context: PortalLifecycleManager) = synchronizedIfNull(
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
  noinline creator: NoArgBindingDI<PortalLifecycleManager>.() -> T
): Singleton<PortalLifecycleManager, T> = scoped(PortalScope).singleton(creator = creator)

public inline fun <reified A : Any, reified T : Any> DI.Builder.portalMultiton(
  noinline creator: BindingDI<PortalLifecycleManager>.(A) -> T
): Multiton<PortalLifecycleManager, A, T> = scoped(PortalScope).multiton(creator = creator)

public inline fun <reified T : Any> DI.Builder.bindPortalSingleton(
  tag: Any? = null,
  overrides: Boolean? = null,
  noinline creator: NoArgBindingDI<PortalLifecycleManager>.() -> T
) {
  bind<T>(
    tag = tag,
    overrides = overrides
  ) with portalSingleton(creator)
}

public inline fun <reified A : Any, reified T : Any> DI.Builder.bindPortalMultiton(
  tag: Any? = null,
  overrides: Boolean? = null,
  noinline creator: BindingDI<PortalLifecycleManager>.(A) -> T
) {
  bind<T>(
    tag = tag,
    overrides = overrides
  ) with portalMultiton(creator)
}
