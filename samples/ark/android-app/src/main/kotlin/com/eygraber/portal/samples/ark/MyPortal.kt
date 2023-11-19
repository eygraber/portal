package com.eygraber.portal.samples.ark

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.samples.ark.ViceCompositor.Companion.isBackHandlerEnabled
import com.eygraber.portal.samples.ark.ViceCompositor.Companion.onBackPressed
import com.eygraber.portal.samples.ark.ViceCompositor.Companion.onIntent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class MyPortal<K, V, I, C, E, S> : ComposePortal<K>
  where K : Nav, V : ViceView<I, S>, C : ViceCompositor<I, S>, E : ViceEffects {
  protected abstract val compositor: C

  @Composable
  final override fun Render() {
    val scope = rememberCoroutineScope {
      Dispatchers.Main.immediate
    }

    BackHandler(enabled = compositor.isBackHandlerEnabled()) {
      compositor.onBackPressed { intent ->
        // this is synchronous because the dispatcher is Main.immediate
        scope.launch {
          compositor.onIntent(intent)
        }
      }
    }
  }
}
