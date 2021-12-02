package com.eygraber.portal.samples.kotlin.inject.main

import androidx.compose.runtime.Composable
import com.eygraber.portal.ParentPortal
import com.eygraber.portal.samples.kotlin.inject.AppScope
import com.eygraber.portal.samples.kotlin.inject.InjectablePortal
import com.eygraber.portal.samples.kotlin.inject.appComponent
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class MainPortal : ParentPortal, InjectablePortal<MainComponent> {
  override val component = MainComponent::class.create(appComponent, this)

  private val appPortalManager by lazy {
    component.appPortalManager
  }

  private val mainBottomNavPortalManager by lazy {
    component.mainBottomNavPortalManager
  }

  private val mainView: MainView by lazy {
    component.view
  }

  override val portalManagers by lazy {
    listOf(appPortalManager, mainBottomNavPortalManager)
  }

  @Composable
  override fun render() {
    mainView.render()
  }
}
