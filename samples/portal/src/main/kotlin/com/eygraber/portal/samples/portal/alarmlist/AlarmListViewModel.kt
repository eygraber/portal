package com.eygraber.portal.samples.portal.alarmlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.samples.portal.VM
import com.eygraber.portal.samples.portal.main.AppPortalKey
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.UUID

class AlarmListViewModel(
  private val appPortalManager: ComposePortalManager<AppPortalKey>,
) : VM<AlarmListState> {
  private class MutableAlarmListAlarm(
    id: String,
    isEnabled: Boolean,
    time: String,
  ) : AlarmListAlarm {
    override var id by mutableStateOf(id)
    override var isEnabled by mutableStateOf(isEnabled)
    override var time by mutableStateOf(time)
  }

  private val mutableState = object : AlarmListState {
    override var alarms by mutableStateOf(
      listOf(
        MutableAlarmListAlarm(
          id = UUID.randomUUID().toString(),
          isEnabled = true,
          time = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
        ),
      ),
    )
  }
  override val state = mutableState

  fun backClicked() {
    appPortalManager.withTransaction {
      backstack.pop()
    }
  }

  fun addAlarmClicked() {
    mutableState.alarms = mutableState.alarms +
      MutableAlarmListAlarm(
        id = UUID.randomUUID().toString(),
        isEnabled = true,
        time = LocalTime.now().format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)),
      )
  }

  fun alarmEnabledChanged(alarm: AlarmListAlarm) {
    mutableState
      .alarms
      .find { it.id == alarm.id }
      ?.let {
        it.isEnabled = !it.isEnabled
      }
  }
}
