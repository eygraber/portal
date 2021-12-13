package com.eygraber.portal.samples.portal.home

import com.eygraber.portal.compose.PortalManager
import com.eygraber.portal.push
import com.eygraber.portal.samples.portal.VM
import com.eygraber.portal.samples.portal.alarmlist.AlarmListPortal
import com.eygraber.portal.samples.portal.main.AppPortalKey

class HomeViewModel(
  private val appPortalManager: PortalManager<AppPortalKey>,
  private val alarmListPortalProvider: () -> AlarmListPortal
) : VM<Unit> {
  override val state = Unit

  fun openAlarmsClicked() {
    appPortalManager.withTransaction {
      backstack.push(AppPortalKey.AlarmList) {
        add(AppPortalKey.AlarmList, portal = alarmListPortalProvider())
      }
    }
  }
}
