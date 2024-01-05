package com.eygraber.portal.samples.portal

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.portal.samples.portal.main.MainPortal
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.provider
import javax.swing.UIManager

private val applicationDI by DI.lazy {
  bind<MainPortal>() with provider {
    MainPortal(di)
  }
}

fun main() {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  val mainPortal = applicationDI.direct.instance<MainPortal>()
  singleWindowApplication(title = "Portal") {
    MaterialTheme(
      colors = darkColors(
        primary = Color(0xFFBB86FC),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC5),
      ),
    ) {
      Surface {
        mainPortal.Render()
      }
    }
  }
}
