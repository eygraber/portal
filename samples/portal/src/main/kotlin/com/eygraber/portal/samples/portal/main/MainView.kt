package com.eygraber.portal.samples.portal.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.samples.icons.FeaturedVideo
import com.eygraber.portal.samples.icons.Home
import com.eygraber.portal.samples.icons.Icons
import com.eygraber.portal.samples.icons.Restaurant
import com.eygraber.portal.samples.icons.WhatsHot
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
        icon = Icons.Home,
        onClick = vm::handle1Clicked
      )

      BottomNavIcon(
        icon = Icons.WhatsHot,
        onClick = vm::handle2Clicked
      )

      BottomNavIcon(
        icon = Icons.Restaurant,
        onClick = vm::handle3Clicked
      )

      BottomNavIcon(
        icon = Icons.FeaturedVideo,
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
