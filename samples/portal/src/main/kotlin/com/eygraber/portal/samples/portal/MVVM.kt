package com.eygraber.portal.samples.portal

import androidx.compose.runtime.Composable

public interface View<State> {
  public val vm: VM<State>

  @Composable
  public fun render()
}

public interface VM<State> {
  public val state: State
}
