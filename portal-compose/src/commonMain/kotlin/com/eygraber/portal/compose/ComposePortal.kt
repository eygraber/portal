package com.eygraber.portal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.eygraber.portal.Portal

@Immutable
public interface ComposePortal : Portal {
  @Composable
  public fun Render()
}
