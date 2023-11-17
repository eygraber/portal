package com.eygraber.portal.samples.ark

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

interface ArkPresenter<Intent, State> {
  @Composable
  fun initialize(intents: Flow<Intent>): State {
    LaunchedEffect(Unit) {
      intents.collect { intent ->
        onIntent(intent)
      }
    }

    return present(intents)
  }

  @Composable
  fun present(intents: Flow<Intent>): State

  suspend fun onIntent(intent: Intent) {}
}
