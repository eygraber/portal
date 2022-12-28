package com.eygraber.portal.samples.kotlin.inject

import com.eygraber.portal.Portal
import me.tatarka.inject.annotations.Provides

interface PortalComponent<PortalT : Portal> {
  @get:Provides val portal: PortalT
}

interface InjectablePortal<T : PortalComponent<*>> {
  val component: T
}
