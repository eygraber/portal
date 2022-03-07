package com.eygraber.portal.samples.kotlin.inject.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.eygraber.portal.compose.PortalManager
import com.eygraber.portal.samples.kotlin.inject.View
import me.tatarka.inject.annotations.Inject

@MainScope
@Inject
class MainView(
  private val appPortalManager: PortalManager<AppPortalKey>,
  private val mainBottomNavPortalManager: PortalManager<MainBottomNavPortalKey>,
  override val vm: MainViewModel
) : View<MainState> {
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
        Box(
          modifier = Modifier
            .fillMaxSize()
        ) {
          BottomNav()
        }
        appPortalManager.Render()
      }
    }
  }

  @Composable
  private fun BottomNav() {
    Column(
      modifier = Modifier
        .fillMaxSize()
    ) {
      BottomNavPortal()
      BottomNavIcons()
    }
  }

  @Composable
  private fun ColumnScope.BottomNavPortal() {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .weight(.90F)
    ) {
      mainBottomNavPortalManager.Render()
    }
  }

  @Composable
  private fun ColumnScope.BottomNavIcons() {
    Row(
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .fillMaxWidth()
        .weight(.10F)
    ) {
      BottomNavIcon(
        icon = Icons.Filled.Home,
        onClick = vm::handle1Clicked
      )

      BottomNavIcon(
        icon = Icons.Filled.Whatshot,
        onClick = vm::handle2Clicked
      )

      BottomNavIcon(
        icon = Icons.Filled.Restaurant,
        onClick = vm::handle3Clicked
      )

      BottomNavIcon(
        icon = Icons.Filled.FeaturedVideo,
        onClick = vm::handle4Clicked
      )
    }
  }

  @Composable
  private fun BottomNavIcon(
    icon: ImageVector,
    onClick: () -> Unit
  ) {
    Button(
      onClick = onClick
    ) {
      Image(icon, "content")
    }
  }
}
