package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.runtime.Immutable

@Immutable
public data class ComposePortalTransition(
  val enter: EnterTransition,
  val exit: ExitTransition
) {
  public companion object {
    public fun enter(
      enter: EnterTransition
    ): ComposePortalTransition = ComposePortalTransition(
      enter = enter,
      exit = ExitTransition.None
    )

    public fun exit(
      exit: ExitTransition
    ): ComposePortalTransition = ComposePortalTransition(
      enter = EnterTransition.None,
      exit = exit
    )

    public val defaultEnter: EnterTransition = slideInHorizontally(
      initialOffsetX = { it * 2 }
    )

    public val defaultExit: ExitTransition = slideOutHorizontally(
      targetOffsetX = { -it }
    )

    public val defaultEnterForBackstack: EnterTransition = fadeIn()

    public val defaultExitForBackstack: ExitTransition = fadeOut()

    public val defaultAttach: EnterTransition = slideInVertically(
      initialOffsetY = { -it }
    )

    public val defaultDetach: ExitTransition = slideOutVertically(
      targetOffsetY = { it * 2 }
    )

    public val defaultAttachForBackstack: EnterTransition = fadeIn()

    public val defaultDetachForBackstack: ExitTransition = fadeOut()

    public val None: ComposePortalTransition = ComposePortalTransition(
      enter = EnterTransition.None,
      exit = ExitTransition.None,
    )
  }
}
