package com.eygraber.portal.samples.portal.alarmlist

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.portal.samples.portal.View

class AlarmListView(
  override val vm: AlarmListViewModel
) : View<AlarmListState> {
  @Composable
  override fun render() {
    Surface {
      Column {
        Row {
          IconButton(
            onClick = { vm.backClicked() }
          ) {
            Icon(
              imageVector = Icons.Filled.ArrowBack,
              contentDescription = "Back"
            )
          }

          Text(
            text = "Alarm",
            modifier = Modifier
              .align(Alignment.CenterVertically)
              .padding(start = 16.dp)
          )
        }
        Button(
          onClick = { vm.addAlarmClicked() }
        ) {
          Text("Add Alarm")
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          LazyColumn {
            items(vm.state.alarms, key = AlarmListAlarm::id) { alarm ->
              AlarmItem(alarm)
            }
          }
        }
      }
    }
  }

  @Composable
  private fun AlarmItem(alarm: AlarmListAlarm) {
    println("Composing item ${alarm.id}")
    Row {
      Switch(
        checked = alarm.isEnabled,
        onCheckedChange = {
          vm.alarmEnabledChanged(alarm)
        },
        modifier = Modifier
          .align(Alignment.CenterVertically)
      )

      Text(
        text = alarm.time,
        modifier = Modifier
          .align(Alignment.CenterVertically)
          .padding(start = 8.dp)
      )
    }
  }
}
