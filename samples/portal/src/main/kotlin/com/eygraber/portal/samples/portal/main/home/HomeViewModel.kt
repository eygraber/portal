package com.eygraber.portal.samples.portal.main.home

import com.eygraber.portal.push
import com.eygraber.portal.samples.portal.AppPortalKey
import com.eygraber.portal.samples.portal.VM
import com.eygraber.portal.samples.portal.appPortalManager
import com.eygraber.portal.samples.portal.main.alarmlist.AlarmListView

class HomeViewModel : VM<Unit> {
  override val state = Unit

  fun openAlarmsClicked() {
    appPortalManager.withTransaction {
      backstack.push(AppPortalKey.AlarmList) {
        add(AppPortalKey.AlarmList) {
          AlarmListView().render()
        }
      }
    }
  }
}
