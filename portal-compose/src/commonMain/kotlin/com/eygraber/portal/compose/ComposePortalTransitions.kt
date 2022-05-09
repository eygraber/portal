@file:OptIn(ExperimentalAnimationApi::class)

package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import com.eygraber.portal.EnterTransitionOverride
import com.eygraber.portal.ExitTransitionOverride
import com.eygraber.portal.PortalTransitionOverride

public inline fun enterTransitionOverride(
  crossinline transition: () -> EnterTransition
): EnterTransitionOverride.Custom = EnterTransitionOverride.Custom { transition() }

public fun enterTransitionOverride(
  vararg override: EnterTransitionOverride
): EnterTransitionOverride = EnterTransitionOverride.Custom {
  override
    .map(EnterTransitionOverride::toComposeTransition)
    .reduce { acc, enterTransition ->
      acc + enterTransition
    }
}

public inline fun exitTransitionOverride(
  crossinline transition: () -> ExitTransition
): ExitTransitionOverride.Custom = ExitTransitionOverride.Custom { transition() }

public fun exitTransitionOverride(
  vararg override: ExitTransitionOverride
): ExitTransitionOverride = ExitTransitionOverride.Custom {
  override
    .map(ExitTransitionOverride::toComposeTransition)
    .reduce { acc, exitTransition ->
      acc + exitTransition
    }
}

internal fun EnterTransitionOverride.toComposeTransition(): EnterTransition {
  val tweenDuration = duration?.inWholeMilliseconds?.toInt()

  return when(this) {
    EnterTransitionOverride.None -> EnterTransition.None

    is EnterTransitionOverride.ExpandIn -> when(tweenDuration) {
      null -> expandIn(
        expandFrom = expandFrom.toCompose()
      )
      else -> expandIn(
        animationSpec = tween(tweenDuration),
        expandFrom = expandFrom.toCompose()
      )
    }

    is EnterTransitionOverride.ExpandInHorizontally -> when(tweenDuration) {
      null -> expandHorizontally(
        expandFrom = expandFrom.toCompose()
      )
      else -> expandHorizontally(
        animationSpec = tween(tweenDuration),
        expandFrom = expandFrom.toCompose()
      )
    }

    is EnterTransitionOverride.ExpandInVertically -> when(tweenDuration) {
      null -> expandVertically(
        expandFrom = expandFrom.toCompose()
      )
      else -> expandVertically(
        animationSpec = tween(tweenDuration),
        expandFrom = expandFrom.toCompose()
      )
    }

    is EnterTransitionOverride.FadeIn -> when(tweenDuration) {
      null -> fadeIn(initialAlpha = initialAlpha)
      else -> fadeIn(
        animationSpec = tween(tweenDuration),
        initialAlpha = initialAlpha
      )
    }

    is EnterTransitionOverride.ScaleIn -> when(tweenDuration) {
      null -> scaleIn(initialScale = initialScale)
      else -> scaleIn(
        animationSpec = tween(tweenDuration),
        initialScale = initialScale
      )
    }

    is EnterTransitionOverride.SlideInFromBottom -> when(tweenDuration) {
      null -> slideInVertically { it * 2 }
      else -> slideInVertically(
        animationSpec = tween(tweenDuration)
      ) { it * 2 }
    }

    is EnterTransitionOverride.SlideInFromLeft -> when(tweenDuration) {
      null -> slideInHorizontally { -it }
      else -> slideInHorizontally(
        animationSpec = tween(tweenDuration)
      ) { -it }
    }

    is EnterTransitionOverride.SlideInFromRight -> when(tweenDuration) {
      null -> slideInHorizontally { it * 2 }
      else -> slideInHorizontally(
        animationSpec = tween(tweenDuration)
      ) { it * 2 }
    }

    is EnterTransitionOverride.SlideInFromTop -> when(tweenDuration) {
      null -> slideInVertically { -it }
      else -> slideInVertically(
        animationSpec = tween(tweenDuration)
      ) { -it }
    }

    is EnterTransitionOverride.Custom -> when(val custom = custom()) {
      is EnterTransition -> custom
      else -> error("Custom enter override for compose needs to be an EnterTransition")
    }
  }
}

internal fun ExitTransitionOverride.toComposeTransition(): ExitTransition {
  val tweenDuration = duration?.inWholeMilliseconds?.toInt()

  return when(this) {
    ExitTransitionOverride.None -> ExitTransition.None

    is ExitTransitionOverride.FadeOut -> when(tweenDuration) {
      null -> fadeOut(
        targetAlpha = targetAlpha
      )
      else -> fadeOut(
        animationSpec = tween(tweenDuration),
        targetAlpha = targetAlpha
      )
    }

    is ExitTransitionOverride.ScaleOut -> when(tweenDuration) {
      null -> scaleOut(
        targetScale = targetScale
      )
      else -> scaleOut(
        animationSpec = tween(tweenDuration),
        targetScale = targetScale
      )
    }

    is ExitTransitionOverride.ShrinkOut -> when(tweenDuration) {
      null -> shrinkOut(
        shrinkTowards = shrinkTowards.toCompose()
      )
      else -> shrinkOut(
        animationSpec = tween(tweenDuration),
        shrinkTowards = shrinkTowards.toCompose()
      )
    }

    is ExitTransitionOverride.ShrinkOutHorizontally -> when(tweenDuration) {
      null -> shrinkHorizontally(
        shrinkTowards = shrinkTowards.toCompose()
      )
      else -> shrinkHorizontally(
        animationSpec = tween(tweenDuration),
        shrinkTowards = shrinkTowards.toCompose()
      )
    }

    is ExitTransitionOverride.ShrinkOutVertically -> when(tweenDuration) {
      null -> shrinkVertically(
        shrinkTowards = shrinkTowards.toCompose()
      )
      else -> shrinkVertically(
        animationSpec = tween(tweenDuration),
        shrinkTowards = shrinkTowards.toCompose()
      )
    }

    is ExitTransitionOverride.SlideOutToBottom -> when(tweenDuration) {
      null -> slideOutVertically { it * 2 }
      else -> slideOutVertically(
        animationSpec = tween(tweenDuration)
      ) { it * 2 }
    }

    is ExitTransitionOverride.SlideOutToLeft -> when(tweenDuration) {
      null -> slideOutHorizontally { -it }
      else -> slideOutHorizontally(
        animationSpec = tween(tweenDuration)
      ) { -it }
    }

    is ExitTransitionOverride.SlideOutToRight -> when(tweenDuration) {
      null -> slideOutHorizontally { it * 2 }
      else -> slideOutHorizontally(
        animationSpec = tween(tweenDuration)
      ) { it * 2 }
    }

    is ExitTransitionOverride.SlideOutToTop -> when(tweenDuration) {
      null -> slideOutVertically { -it }
      else -> slideOutVertically(
        animationSpec = tween(tweenDuration)
      ) { -it }
    }

    is ExitTransitionOverride.Custom -> when(val custom = custom()) {
      is ExitTransition -> custom
      else -> error("Custom exit override for compose needs to be an ExitTransition")
    }
  }
}

private fun PortalTransitionOverride.Alignment.Horizontal.toCompose() = when(this) {
  PortalTransitionOverride.Alignment.Horizontal.Start -> Alignment.Start
  PortalTransitionOverride.Alignment.Horizontal.Center -> Alignment.CenterHorizontally
  PortalTransitionOverride.Alignment.Horizontal.End -> Alignment.End
}

private fun PortalTransitionOverride.Alignment.Vertical.toCompose() = when(this) {
  PortalTransitionOverride.Alignment.Vertical.Top -> Alignment.Top
  PortalTransitionOverride.Alignment.Vertical.Center -> Alignment.CenterVertically
  PortalTransitionOverride.Alignment.Vertical.Bottom -> Alignment.Bottom
}

private fun PortalTransitionOverride.Alignment.All.toCompose() = when(this) {
  PortalTransitionOverride.Alignment.All.TopStart -> Alignment.TopStart
  PortalTransitionOverride.Alignment.All.TopCenter -> Alignment.TopCenter
  PortalTransitionOverride.Alignment.All.TopEnd -> Alignment.TopEnd
  PortalTransitionOverride.Alignment.All.CenterStart -> Alignment.CenterStart
  PortalTransitionOverride.Alignment.All.Center -> Alignment.Center
  PortalTransitionOverride.Alignment.All.CenterEnd -> Alignment.CenterEnd
  PortalTransitionOverride.Alignment.All.BottomStart -> Alignment.BottomStart
  PortalTransitionOverride.Alignment.All.BottomCenter -> Alignment.BottomCenter
  PortalTransitionOverride.Alignment.All.BottomEnd -> Alignment.BottomEnd
}
