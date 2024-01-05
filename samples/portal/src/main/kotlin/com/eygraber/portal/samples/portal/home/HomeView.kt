package com.eygraber.portal.samples.portal.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.eygraber.portal.samples.icons.Alarm
import com.eygraber.portal.samples.icons.Icons
import com.eygraber.portal.samples.portal.View

class HomeView(
  override val vm: HomeViewModel,
) : View<Unit> {
  @Composable
  override fun Render() {
    Box(
      contentAlignment = Alignment.Center,
      modifier = Modifier.fillMaxSize(),
    ) {
      Button(
        onClick = vm::openAlarmsClicked,
      ) {
        Icon(
          imageVector = Icons.Alarm,
          contentDescription = "Open alarm list",
        )
      }
    }
  }
}
