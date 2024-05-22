package com.eygraber.portal.samples.simpleportal.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NumberBox(text: String) {
  Box(modifier = Modifier.fillMaxSize()) {
    Text(text, modifier = Modifier.align(Alignment.Center))
  }
}
