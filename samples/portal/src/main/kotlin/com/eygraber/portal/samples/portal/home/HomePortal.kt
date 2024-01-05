package com.eygraber.portal.samples.portal.home

import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.kodein.di.KodeinDIPortal
import com.eygraber.portal.kodein.di.portalSingleton
import com.eygraber.portal.samples.portal.alarmlist.AlarmListPortal
import com.eygraber.portal.samples.portal.main.MainPortal
import com.eygraber.portal.samples.portal.main.MainPortalKey
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.on
import org.kodein.di.provider

class HomePortal(
  override val parent: ParentPortal,
) : ComposePortal<MainPortalKey>, KodeinDIPortal(), ChildPortal {
  override val key = MainPortalKey.One

  private val homeView by on(context = this).instance<HomeView>()

  override fun provideModule() = DI.Module("Home") {
    bind<AlarmListPortal>() with provider {
      AlarmListPortal(
        parent = parent,
      )
    }

    bind<HomeView>() with portalSingleton {
      HomeView(
        vm = instance(),
      )
    }

    bind<HomeViewModel>() with portalSingleton {
      HomeViewModel(
        appPortalManager = on(context = parent as MainPortal).instance(),
        alarmListPortalProvider = provider(),
      )
    }
  }

  @Composable
  override fun Render() {
    homeView.Render()
  }
}
