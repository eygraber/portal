package com.eygraber.portal.samples.kotlin.inject.alarmlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.samples.kotlin.inject.VM
import com.eygraber.portal.samples.kotlin.inject.main.AppPortalKey
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@AlarmListScope
@Inject
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

  @OptIn(ExperimentalTime::class)
  private val mutableState = object : AlarmListState {
    override var alarms by mutableStateOf(
      listOf(
        MutableAlarmListAlarm(
          id = Random.nextLong().toString(),
          isEnabled = true,
          time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.toString(),
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

  @OptIn(ExperimentalTime::class)
  fun addAlarmClicked() {
    mutableState.alarms = mutableState.alarms +
      MutableAlarmListAlarm(
        id = Random.nextLong().toString(),
        isEnabled = true,
        time = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).time.toString(),
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
