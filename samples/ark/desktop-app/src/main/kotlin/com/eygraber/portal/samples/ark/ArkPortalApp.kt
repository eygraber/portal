package com.eygraber.portal.samples.ark

import androidx.compose.ui.window.singleWindowApplication
import javax.swing.UIManager

fun main() {
  val portal = SampleArkPortal()
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Portal") {
    portal.Render()
  }
}
