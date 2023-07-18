package com.eygraber.portal.samples.icons

import androidx.compose.ui.graphics.vector.ImageVector

val Icons.FeaturedVideo: ImageVector by lazy {
  materialIcon(name = "Filled.FeaturedVideo") {
    materialPath {
      moveTo(21.0f, 3.0f)
      lineTo(3.0f, 3.0f)
      curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
      verticalLineToRelative(14.0f)
      curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
      horizontalLineToRelative(18.0f)
      curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
      lineTo(23.0f, 5.0f)
      curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
      close()
      moveTo(12.0f, 12.0f)
      lineTo(3.0f, 12.0f)
      lineTo(3.0f, 5.0f)
      horizontalLineToRelative(9.0f)
      verticalLineToRelative(7.0f)
      close()
    }
  }
}
