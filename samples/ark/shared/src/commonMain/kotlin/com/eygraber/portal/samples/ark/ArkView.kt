package com.eygraber.portal.samples.ark

import androidx.compose.runtime.Composable

interface ArkView<Intent, State> {
  @Composable
  fun Render(state: State, onIntent: (Intent) -> Unit)
}
