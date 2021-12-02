package com.eygraber.portal.samples.kotlin.inject.alarmlist

import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.samples.kotlin.inject.InjectablePortal
import com.eygraber.portal.samples.kotlin.inject.home.HomePortal
import me.tatarka.inject.annotations.Inject

@Inject
class AlarmListPortal(
  override val parent: HomePortal
) : ChildPortal, InjectablePortal<AlarmListComponent> {
  override val component = AlarmListComponent::class.create(parent.component, this)

  private val view by lazy {
    component.view
  }

  @Composable
  override fun render() {
    view.render()
  }
}
