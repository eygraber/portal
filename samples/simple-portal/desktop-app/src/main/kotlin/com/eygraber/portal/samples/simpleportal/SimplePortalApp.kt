package com.eygraber.portal.samples.simpleportal

import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.portal.samples.simpleportal.shared.SimplePortal
import javax.swing.UIManager

fun main() {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Simple Portal") {
    SimplePortal()
  }
}
