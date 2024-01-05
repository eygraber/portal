package com.eygraber.portal.samples.kotlin.inject.alarmlist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eygraber.portal.samples.icons.ArrowBack
import com.eygraber.portal.samples.icons.Icons
import com.eygraber.portal.samples.kotlin.inject.View
import me.tatarka.inject.annotations.Inject

@AlarmListScope
@Inject
class AlarmListView(
  override val vm: AlarmListViewModel,
) : View<AlarmListState> {
  @Composable
  override fun Render() {
    Surface {
      Column {
        Row {
          IconButton(
            onClick = { vm.backClicked() },
          ) {
            Icon(
              imageVector = Icons.ArrowBack,
              contentDescription = "Back",
            )
          }

          Text(
            text = "Alarm",
            modifier = Modifier
              .align(Alignment.CenterVertically)
              .padding(start = 16.dp),
          )
        }
        Button(
          onClick = { vm.addAlarmClicked() },
        ) {
          Text("Add Alarm")
        }

        AlarmItems()
      }
    }
  }

  @Composable
  private fun AlarmItems() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      LazyColumn {
        items(vm.state.alarms, key = AlarmListAlarm::id) { alarm ->
          AlarmItem(alarm)
        }
      }
    }
  }

  @Composable
  private fun AlarmItem(alarm: AlarmListAlarm) {
    val visibleState = remember {
      MutableTransitionState(false)
        .apply { targetState = true }
    }
    AnimatedVisibility(
      visibleState = visibleState,
      enter = fadeIn(),
    ) {
      Row {
        Switch(
          checked = alarm.isEnabled,
          onCheckedChange = {
            vm.alarmEnabledChanged(alarm)
          },
          modifier = Modifier
            .align(Alignment.CenterVertically),
        )

        Text(
          text = alarm.time,
          modifier = Modifier
            .align(Alignment.CenterVertically)
            .padding(start = 8.dp),
        )
      }
    }
  }
}
