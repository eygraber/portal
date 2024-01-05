package com.eygraber.portal.samples.kotlin.inject

import com.eygraber.portal.Portal

interface PortalComponent<PortalT : Portal> {
  val portal: PortalT
}

interface InjectablePortal<T : PortalComponent<*>> {
  val component: T
}
