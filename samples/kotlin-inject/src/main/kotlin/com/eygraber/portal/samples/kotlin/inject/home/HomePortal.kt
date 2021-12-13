package com.eygraber.portal.samples.kotlin.inject.home

import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.samples.kotlin.inject.InjectablePortal
import com.eygraber.portal.samples.kotlin.inject.main.MainPortal
import com.eygraber.portal.samples.kotlin.inject.main.MainScope
import me.tatarka.inject.annotations.Inject

@MainScope
@Inject
class HomePortal(
  override val parent: MainPortal
) : ComposePortal, ChildPortal, InjectablePortal<HomeComponent> {
  override val component = HomeComponent::class.create(parent.component, this)

  private val view by lazy {
    component.view
  }

  @Composable
  override fun render() {
    view.render()
  }
}
