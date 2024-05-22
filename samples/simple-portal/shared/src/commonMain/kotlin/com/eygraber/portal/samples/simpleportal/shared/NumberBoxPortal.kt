package com.eygraber.portal.samples.simpleportal.shared

import androidx.compose.runtime.Composable
import com.eygraber.portal.compose.ComposePortal

class NumberBoxPortal(
  override val key: PortalKey,
) : ComposePortal<PortalKey> {
  @Composable
  override fun Render() {
    NumberBox(key.value)
  }
}
