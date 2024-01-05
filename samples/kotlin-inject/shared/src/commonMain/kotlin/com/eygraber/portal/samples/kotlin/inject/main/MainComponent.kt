package com.eygraber.portal.samples.kotlin.inject.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.compose.PortalTransition
import com.eygraber.portal.samples.kotlin.inject.AppComponent
import com.eygraber.portal.samples.kotlin.inject.PortalComponent
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope

@Scope
annotation class MainScope

@MainScope
@Component
abstract class MainComponent(
  @Component @get:Provides val appComponent: AppComponent,
  @get:Provides override val portal: MainPortal,
) : PortalComponent<MainPortal> {
  abstract val view: MainView
  abstract val appPortalManager: ComposePortalManager<AppPortalKey>
  abstract val mainBottomNavPortalManager: ComposePortalManager<MainBottomNavPortalKey>

  @MainScope
  @Provides
  fun providesAppPortalManager() =
    ComposePortalManager<AppPortalKey>(
      defaultTransitionProvider = { _, _ ->
        PortalTransition(
          enter = slideInVertically(animationSpec = tween(durationMillis = 400)) { it * 2 },
          exit = slideOutVertically(animationSpec = tween(durationMillis = 750)) { it * 2 },
        )
      },
      defaultErrorHandler = {
        it.printStackTrace()
      },
    )

  @MainScope
  @Provides
  fun mainPortalManager() =
    ComposePortalManager<MainBottomNavPortalKey>(
      defaultErrorHandler = {
        it.printStackTrace()
      },
    )
}
