package com.eygraber.portal.samples.portal.root

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.compose.PortalManager
import com.eygraber.portal.kodein.di.KodeinRootPortal
import com.eygraber.portal.samples.portal.main.MainPortal
import org.kodein.di.DI
import org.kodein.di.instance

class AppRootPortal(di: DI) : ComposePortal, KodeinRootPortal(di) {
  override val portalManagers = emptyList<PortalManager<*>>()

  private val mainPortal by instance<MainPortal>()

  @Composable
  override fun Render() {
    MaterialTheme(
      colors = darkColors(
        primary = Color(0xFFBB86FC),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC5)
      )
    ) {
      Surface {
        mainPortal.Render()
      }
    }
  }
}
