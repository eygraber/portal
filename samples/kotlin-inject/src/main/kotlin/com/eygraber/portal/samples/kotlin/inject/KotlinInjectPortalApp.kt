package com.eygraber.portal.samples.kotlin.inject

import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.portal.renderWithAnimatedVisibility
import javax.swing.UIManager

fun main() {
  val mainPortal = appComponent.mainPortal
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Portal") {
    mainPortal.renderWithAnimatedVisibility()
  }
}
