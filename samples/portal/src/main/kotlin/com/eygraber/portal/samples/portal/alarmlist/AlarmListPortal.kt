package com.eygraber.portal.samples.portal.alarmlist

import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.kodein.di.KodeinDIPortal
import com.eygraber.portal.kodein.di.portalSingleton
import com.eygraber.portal.samples.portal.main.AppPortalKey
import com.eygraber.portal.samples.portal.main.MainPortal
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.on

class AlarmListPortal(
  override val parent: ParentPortal,
) : ComposePortal<AppPortalKey>, KodeinDIPortal(), ChildPortal {
  override val key = AppPortalKey.AlarmList

  private val alarmListView by on(context = this).instance<AlarmListView>()

  override fun provideModule() = DI.Module("AlarmList") {
    bind<AlarmListView>() with portalSingleton {
      AlarmListView(
        vm = instance(),
      )
    }

    bind<AlarmListViewModel>() with portalSingleton {
      AlarmListViewModel(
        appPortalManager = on(context = parent as MainPortal).instance(),
      )
    }
  }

  @Composable
  override fun Render() {
    alarmListView.Render()
  }
}
