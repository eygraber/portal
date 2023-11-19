package com.eygraber.portal.samples.ark

import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.Flow

abstract class ViceCompositor<Intent, State> {
  @Composable
  protected abstract fun composite(intents: Flow<Intent>): State

  protected open suspend fun onIntent(intent: Intent) {}

  @Composable
  protected open fun isBackHandlerEnabled() = false

  protected open fun onBackPressed(emitIntent: (Intent) -> Unit) {}

  // this is how composite/onIntent can be protected
  // but also visible to MyPortal so they can be invoked
  companion object {
    @Suppress("NOTHING_TO_INLINE")
    @Composable
    internal inline fun <Intent, State> ViceCompositor<Intent, State>.composite(
      intents: Flow<Intent>
    ) = composite(intents)

    internal suspend inline fun <Intent, State> ViceCompositor<Intent, State>.onIntent(
      intent: Intent
    ) = onIntent(intent)

    @Composable
    internal fun <Intent, State> ViceCompositor<Intent, State>.isBackHandlerEnabled() = isBackHandlerEnabled()

    internal fun <Intent, State> ViceCompositor<Intent, State>.onBackPressed(
      emitIntent: (Intent) -> Unit
    ) = onBackPressed(emitIntent)
  }
}
