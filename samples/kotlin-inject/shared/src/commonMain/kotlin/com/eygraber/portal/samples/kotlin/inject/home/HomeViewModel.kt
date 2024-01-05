package com.eygraber.portal.samples.kotlin.inject.home

import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.push
import com.eygraber.portal.samples.kotlin.inject.VM
import com.eygraber.portal.samples.kotlin.inject.alarmlist.AlarmListPortal
import com.eygraber.portal.samples.kotlin.inject.main.AppPortalKey
import me.tatarka.inject.annotations.Inject

@HomeScope
@Inject
class HomeViewModel(
  private val appPortalManager: ComposePortalManager<AppPortalKey>,
  private val alarmListPortalProvider: () -> AlarmListPortal,
) : VM<Unit> {
  override val state = Unit

  fun openAlarmsClicked() {
    appPortalManager.withTransaction {
      backstack.push(AppPortalKey.AlarmList) {
        add(portal = alarmListPortalProvider())
      }
    }
  }
}
