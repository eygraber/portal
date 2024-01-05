package com.eygraber.portal.samples.kotlin.inject.alarmlist

import com.eygraber.portal.samples.kotlin.inject.PortalComponent
import com.eygraber.portal.samples.kotlin.inject.main.MainComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Scope

@Scope
annotation class AlarmListScope

@AlarmListScope
@Component
abstract class AlarmListComponent(
  @Component protected val parentComponent: MainComponent,
  override val portal: AlarmListPortal,
) : PortalComponent<AlarmListPortal> {
  abstract val view: AlarmListView
}
