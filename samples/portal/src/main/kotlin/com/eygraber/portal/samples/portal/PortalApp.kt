package com.eygraber.portal.samples.portal

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalTransitions
import com.eygraber.portal.samples.portal.main.MainView
import javax.swing.UIManager

enum class AppPortalKey {
  AlarmList
}

val appPortalManager = PortalManager<AppPortalKey>(
  defaultTransitions = PortalTransitions(
    enter = slideInVertically(animationSpec = tween(durationMillis = 400)) { it * 2 },
    exit = slideOutVertically(animationSpec = tween(durationMillis = 750)) { it * 2 }
  )
)

fun main() {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Portal") {
    MaterialTheme(
      colors = darkColors(
        primary = Color(0xFFBB86FC),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC5)
      )
    ) {
      Surface {
        MainView().render()
      }
    }
  }
}
