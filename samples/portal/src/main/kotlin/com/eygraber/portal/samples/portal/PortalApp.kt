package com.eygraber.portal.samples.portal

import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.portal.samples.portal.main.MainPortal
import com.eygraber.portal.samples.portal.root.AppRootPortal
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.bindSingleton
import org.kodein.di.direct
import org.kodein.di.instance
import org.kodein.di.provider
import javax.swing.UIManager

private val applicationDI by DI.lazy {
  bindSingleton {
    AppRootPortal(di)
  }

  bind<MainPortal>() with provider {
    MainPortal(
      parent = instance<AppRootPortal>()
    )
  }
}

fun main() {
  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Portal") {
    applicationDI.direct.instance<AppRootPortal>().render()
  }
}
