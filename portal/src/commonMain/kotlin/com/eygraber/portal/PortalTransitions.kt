package com.eygraber.portal

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Immutable

public typealias PortalTransitionsProvider<PortalKey> = ((PortalKey) -> PortalTransitions?)?

@Immutable
public data class PortalTransitions(
  val enter: EnterTransition,
  val exit: ExitTransition,
  val sendToBackstack: ExitTransition = exit,
  val restoreFromBackstack: EnterTransition = enter
) {
  public fun getEnterAndExitTransitions(
    isForBackstack: Boolean
  ): Pair<EnterTransition, ExitTransition> = when {
    isForBackstack -> restoreFromBackstack to sendToBackstack

    else -> enter to exit
  }

  public companion object {
    public val Default: PortalTransitions = PortalTransitions(
      enter = slideInHorizontally(
        initialOffsetX = { it * 2 }
      ),
      exit = slideOutHorizontally(
        targetOffsetX = { -it }
      ),
      sendToBackstack = fadeOut(),
      restoreFromBackstack = fadeIn()
    )

    public val None: PortalTransitions = PortalTransitions(
      enter = EnterTransition.None,
      exit = ExitTransition.None
    )
  }
}
