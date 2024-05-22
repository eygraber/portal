package com.eygraber.portal.samples.simpleportal.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.eygraber.portal.samples.simpleportal.shared.SimplePortal

class SimplePortalActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SimplePortal()
    }
  }
}
