package com.eygraber.portal.compose

import androidx.compose.runtime.Composable
import com.eygraber.portal.Portal

public fun interface ComposePortal : Portal {
  @Composable
  public fun Render()
}
