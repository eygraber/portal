package com.eygraber.portal.samples.kotlin.inject

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class KotlinInjectActivity : AppCompatActivity() {
  private val mainPortal = appComponent.mainPortal
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      mainPortal.Render()
    }
  }
}
