package com.eygraber.portal.samples.ark

import androidx.compose.runtime.Composable

interface ViceView<Intent, State> {
  @Composable
  fun Render(state: State, onIntent: (Intent) -> Unit)
}
