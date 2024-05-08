package com.eygraber.portal.samples.kotlin.inject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class KotlinInjectActivity : ComponentActivity() {
  private val mainPortal = appComponent.mainPortal
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      mainPortal.Render()
    }
  }
}
