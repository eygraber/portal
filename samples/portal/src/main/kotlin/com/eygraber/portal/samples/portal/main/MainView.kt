package com.eygraber.portal.samples.portal.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FeaturedVideo
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.samples.portal.View

class MainView(
  private val appPortalManager: ComposePortalManager<AppPortalKey>,
  private val mainPortalManager: ComposePortalManager<MainPortalKey>,
  override val vm: MainViewModel
) : View<MainState> {
  @Composable
  override fun Render() {
    Box(
      modifier = Modifier
        .fillMaxSize()
    ) {
      BottomNav()

      appPortalManager.Render()
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
      mainPortalManager.Render()
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
