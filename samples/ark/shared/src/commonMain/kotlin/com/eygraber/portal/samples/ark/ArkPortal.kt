package com.eygraber.portal.samples.ark

import androidx.compose.runtime.Composable
import com.eygraber.portal.compose.ComposePortal
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow

abstract class ArkPortal<P, V, I, S> : ComposePortal<Unit>
  where P : ArkPresenter<I, S>, V : ArkView<I, S> {
  protected open fun createPresenterScopeContext() = SupervisorJob()

  protected abstract val presenter: P
  protected abstract val view: V

  private val intents = MutableSharedFlow<I>(extraBufferCapacity = 64)

  @Composable
  override fun Render() {
    val model = presenter.initialize(intents = intents)
    view.Render(model) {
      intents.tryEmit(it)
    }
  }
}
