package com.eygraber.portal.samples.kotlin.inject

import androidx.compose.runtime.Composable

interface View<State> {
  val vm: VM<State>

  @Composable
  fun render()
}

interface VM<State> {
  val state: State
}
