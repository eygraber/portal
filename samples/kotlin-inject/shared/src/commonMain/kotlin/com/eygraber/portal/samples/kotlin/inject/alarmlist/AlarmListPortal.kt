package com.eygraber.portal.samples.kotlin.inject.alarmlist

import androidx.compose.runtime.Composable
import com.eygraber.portal.ChildPortal
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.samples.kotlin.inject.InjectablePortal
import com.eygraber.portal.samples.kotlin.inject.main.AppPortalKey
import com.eygraber.portal.samples.kotlin.inject.main.MainPortal
import me.tatarka.inject.annotations.Inject

@Inject
class AlarmListPortal(
  override val parent: MainPortal,
) : ComposePortal<AppPortalKey>, ChildPortal, InjectablePortal<AlarmListComponent> {
  override val key = AppPortalKey.AlarmList

  override val component = AlarmListComponent::class.create(parent.component, this)

  private val view by lazy {
    component.view
  }

  @Composable
  override fun Render() {
    view.Render()
  }
}
