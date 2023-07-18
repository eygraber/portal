package com.eygraber.portal.samples.icons

import androidx.compose.ui.graphics.vector.ImageVector

val Icons.Home: ImageVector by lazy {
  materialIcon(name = "Filled.Home") {
    materialPath {
      moveTo(10.0f, 20.0f)
      verticalLineToRelative(-6.0f)
      horizontalLineToRelative(4.0f)
      verticalLineToRelative(6.0f)
      horizontalLineToRelative(5.0f)
      verticalLineToRelative(-8.0f)
      horizontalLineToRelative(3.0f)
      lineTo(12.0f, 3.0f)
      lineTo(2.0f, 12.0f)
      horizontalLineToRelative(3.0f)
      verticalLineToRelative(8.0f)
      close()
    }
  }
}
