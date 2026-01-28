package com.eygraber.portal.samples.kotlin.inject.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.samples.icons.FeaturedVideo
import com.eygraber.portal.samples.icons.Home
import com.eygraber.portal.samples.icons.Icons
import com.eygraber.portal.samples.icons.Restaurant
import com.eygraber.portal.samples.icons.WhatsHot
import com.eygraber.portal.samples.kotlin.inject.View
import me.tatarka.inject.annotations.Inject

@MainScope
@Inject
class MainView(
  private val appPortalManager: ComposePortalManager<AppPortalKey>,
  private val mainBottomNavPortalManager: ComposePortalManager<MainBottomNavPortalKey>,
  override val vm: MainViewModel,
) : View<MainState> {
  @Composable
  override fun Render() {
    MaterialTheme(
      colorScheme = darkColorScheme(
        primary = Color(0xFFBB86FC),
        secondary = Color(0xFF03DAC5),
        tertiary = Color(0xFF3700B3),
      ),
    ) {
      Surface {
        Box(
          modifier = Modifier
            .fillMaxSize(),
        ) {
          BottomNav()
        }
        @Suppress("UnnecessaryFullyQualifiedName")
        appPortalManager.Render()
      }
    }
  }

  @Composable
  private fun BottomNav() {
    Column(
      modifier = Modifier
        .fillMaxSize(),
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
        .weight(.90F),
    ) {
      @Suppress("UnnecessaryFullyQualifiedName")
      mainBottomNavPortalManager.Render()
    }
  }

  @Composable
  private fun ColumnScope.BottomNavIcons() {
    Row(
      horizontalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
        .fillMaxWidth()
        .weight(.10F),
    ) {
      BottomNavIcon(
        icon = Icons.Home,
        onClick = vm::handle1Clicked,
      )

      BottomNavIcon(
        icon = Icons.WhatsHot,
        onClick = vm::handle2Clicked,
      )

      BottomNavIcon(
        icon = Icons.Restaurant,
        onClick = vm::handle3Clicked,
      )

      BottomNavIcon(
        icon = Icons.FeaturedVideo,
        onClick = vm::handle4Clicked,
      )
    }
  }

  @Composable
  private fun BottomNavIcon(
    icon: ImageVector,
    onClick: () -> Unit,
  ) {
    Button(
      onClick = onClick,
    ) {
      Image(icon, "content")
    }
  }
}
