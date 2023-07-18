package com.eygraber.portal.samples.icons

import androidx.compose.ui.graphics.vector.ImageVector

val Icons.ArrowBack: ImageVector by lazy {
  materialIcon(name = "Filled.ArrowBack") {
    materialPath {
      moveTo(20.0f, 11.0f)
      horizontalLineTo(7.83f)
      lineToRelative(5.59f, -5.59f)
      lineTo(12.0f, 4.0f)
      lineToRelative(-8.0f, 8.0f)
      lineToRelative(8.0f, 8.0f)
      lineToRelative(1.41f, -1.41f)
      lineTo(7.83f, 13.0f)
      horizontalLineTo(20.0f)
      verticalLineToRelative(-2.0f)
      close()
    }
  }
}
