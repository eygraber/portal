package com.eygraber.portal.samples.portal.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.compose.ComposePortalTransition
import com.eygraber.portal.compose.PortalManager
import com.eygraber.portal.kodein.di.KodeinDIPortal
import com.eygraber.portal.kodein.di.portalSingleton
import com.eygraber.portal.samples.portal.home.HomePortal
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.on
import org.kodein.di.provider

class MainPortal(
  override val parent: ParentPortal
) : ComposePortal, KodeinDIPortal(), ParentPortal, ChildPortal {
  private val appPortalManager by on(context = this).instance<PortalManager<AppPortalKey>>()
  private val mainPortalManager by on(context = this).instance<PortalManager<MainPortalKey>>()
  private val mainView by on(context = this).instance<MainView>()

  override val portalManagers by lazy {
    listOf(appPortalManager, mainPortalManager)
  }

  override fun provideModule() = DI.Module("Main") {
    bind<HomePortal>() with portalSingleton {
      HomePortal(
        parent = instance<Portal>() as ParentPortal
      )
    }

    bind<MainView>() with portalSingleton {
      MainView(
        appPortalManager = instance(),
        mainPortalManager = instance(),
        vm = instance()
      )
    }

    bind<MainViewModel>() with portalSingleton {
      MainViewModel(
        mainPortalManager = instance(),
        homePortalProvider = provider()
      )
    }

    bind<PortalManager<AppPortalKey>>() with portalSingleton {
      PortalManager(
        defaultTransitionsProvider = { _, _ ->
          ComposePortalTransition(
            enter = slideInVertically(animationSpec = tween(durationMillis = 400)) { it * 2 },
            exit = slideOutVertically(animationSpec = tween(durationMillis = 750)) { it * 2 }
          )
        },
        defaultErrorHandler = {
          it.printStackTrace()
        }
      )
    }

    bind<PortalManager<MainPortalKey>>() with portalSingleton {
      PortalManager(
        defaultErrorHandler = {
          it.printStackTrace()
        }
      )
    }
  }

  @Composable
  override fun render() {
    mainView.render()
  }
}
