package com.eygraber.portal.samples.portal.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.Portal
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.compose.PortalTransition
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
) : ComposePortal<AppPortalKey>, KodeinDIPortal(), ParentPortal, ChildPortal {
  override val key = AppPortalKey.Main

  private val appPortalManager by on(context = this).instance<ComposePortalManager<AppPortalKey>>()
  private val mainPortalManager by on(context = this).instance<ComposePortalManager<MainPortalKey>>()
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

    bind<ComposePortalManager<AppPortalKey>>() with portalSingleton {
      ComposePortalManager(
        defaultTransitionProvider = { _, _ ->
          PortalTransition(
            enter = slideInVertically(animationSpec = tween(durationMillis = 400)) { it * 2 },
            exit = slideOutVertically(animationSpec = tween(durationMillis = 750)) { it * 2 }
          )
        },
        defaultErrorHandler = {
          it.printStackTrace()
        }
      )
    }

    bind<ComposePortalManager<MainPortalKey>>() with portalSingleton {
      ComposePortalManager(
        defaultErrorHandler = {
          it.printStackTrace()
        }
      )
    }
  }

  @Composable
  override fun Render() {
    mainView.Render()
  }
}
