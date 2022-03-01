package com.eygraber.portal.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.eygraber.portal.Portal

public fun interface ComposePortal : Portal {
  @Composable
  public fun AnimatedVisibilityScope.render()
}

@Composable
public fun ComposePortal.renderWithAnimatedVisibility(
  visibleState: MutableTransitionState<Boolean> = remember {
    MutableTransitionState(false).apply { targetState = true }
  },
  modifier: Modifier = Modifier,
  enter: EnterTransition = fadeIn() + expandIn(),
  exit: ExitTransition = fadeOut() + shrinkOut(),
  label: String = "AnimatedVisibility"
) {
  AnimatedVisibility(
    visibleState = visibleState,
    modifier = modifier,
    enter = enter,
    exit = exit,
    label = label
  ) {
    render()
  }
}
