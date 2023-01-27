package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.eygraber.portal.PortalRendererState

public fun interface PortalTransitionProvider {
  public fun provideTransitions(
    compositionState: PortalRendererState,
    isForBackstack: Boolean
  ): PortalTransition

  public companion object {
    public val None: PortalTransitionProvider = PortalTransitionProvider { _, _ ->
      PortalTransition.None
    }

    public val Default: PortalTransitionProvider =
      PortalTransitionProvider { compositionState, isForBackstack ->
        when(compositionState) {
          PortalRendererState.Added, PortalRendererState.Removed -> when {
            isForBackstack -> PortalTransition(
              enter = PortalTransition.defaultEnterForBackstack,
              exit = PortalTransition.defaultExitForBackstack
            )

            else -> PortalTransition(
              enter = PortalTransition.defaultEnter,
              exit = PortalTransition.defaultExit
            )
          }

          PortalRendererState.Attached, PortalRendererState.Detached -> when {
            isForBackstack -> PortalTransition(
              enter = PortalTransition.defaultAttachForBackstack,
              exit = PortalTransition.defaultDetachForBackstack
            )

            else -> PortalTransition(
              enter = PortalTransition.defaultAttach,
              exit = PortalTransition.defaultDetach
            )
          }
        }
      }

    public fun provideTransitions(
      state: PortalRendererState,
      isForBackstack: Boolean
    ): PortalTransition = Default.provideTransitions(state, isForBackstack)
  }
}

public interface SimplePortalTransitionProvider : PortalTransitionProvider {
  public val enterTransition: EnterTransition
  public val exitTransition: ExitTransition

  public val attachTransition: EnterTransition get() = enterTransition
  public val detachTransition: ExitTransition get() = exitTransition

  public val popEnterTransition: EnterTransition get() = enterTransition
  public val popExitTransition: ExitTransition get() = exitTransition

  public val popAttachTransition: EnterTransition get() = popEnterTransition
  public val popDetachTransition: ExitTransition get() = popExitTransition

  override fun provideTransitions(
    compositionState: PortalRendererState,
    isForBackstack: Boolean
  ): PortalTransition = when(compositionState) {
    PortalRendererState.Added, PortalRendererState.Removed -> when {
      isForBackstack -> PortalTransition(popEnterTransition, popExitTransition)

      else -> PortalTransition(enterTransition, exitTransition)
    }

    PortalRendererState.Attached, PortalRendererState.Detached -> when {
      isForBackstack -> PortalTransition(popAttachTransition, popDetachTransition)

      else -> PortalTransition(attachTransition, detachTransition)
    }
  }
}
