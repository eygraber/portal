package com.eygraber.portal.samples.simpleportal

import androidx.compose.runtime.Composable
import com.eygraber.portal.compose.ComposePortal

class NumberBoxPortal(
  private val number: String
) : ComposePortal {
  @Composable
  override fun Render() {
    NumberBox(number)
  }
}
