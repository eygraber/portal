package com.eygraber.portal.samples.simpleportal.android

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.eygraber.portal.samples.simpleportal.SimplePortal

class SimplePortalActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    setContent {
      SimplePortal()
    }
  }
}
