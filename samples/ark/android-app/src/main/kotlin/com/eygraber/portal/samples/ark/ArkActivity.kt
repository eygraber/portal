package com.eygraber.portal.samples.ark

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class ArkActivity : AppCompatActivity() {
  private val portal = SampleArkPortal()
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      portal.Render()
    }
  }
}
