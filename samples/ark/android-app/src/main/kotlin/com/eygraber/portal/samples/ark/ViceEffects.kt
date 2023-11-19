package com.eygraber.portal.samples.ark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ViceEffects {
  internal fun initialize(scope: CoroutineScope) {
    scope.onInitialized()
  }

  protected abstract fun CoroutineScope.onInitialized()
}

@Composable
internal fun ViceEffects.Launch() {
  LaunchedEffect(Unit) {
    launch(Dispatchers.Default) {
      initialize(this)
    }
  }
}
