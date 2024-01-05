package com.eygraber.portal.samples.kotlin.inject.home

import com.eygraber.portal.samples.kotlin.inject.PortalComponent
import com.eygraber.portal.samples.kotlin.inject.main.MainComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Scope

@Scope
annotation class HomeScope

@HomeScope
@Component
abstract class HomeComponent(
  @Component val parentComponent: MainComponent,
  override val portal: HomePortal,
) : PortalComponent<HomePortal> {
  abstract val view: HomeView
}
