package com.eygraber.portal.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import com.eygraber.portal.KeyedPortal

@Immutable
public interface ComposePortal<KeyT> : KeyedPortal<KeyT> {
  @Composable
  public fun Render()
}
