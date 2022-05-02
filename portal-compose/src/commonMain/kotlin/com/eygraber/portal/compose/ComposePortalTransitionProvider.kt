package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.eygraber.portal.PortalRendererState

public interface ComposePortalTransitions : ComposePortalTransitionsProvider {
  public fun enterTransition(): EnterTransition
  public fun exitTransition(): ExitTransition

  public fun attachTransition(): EnterTransition = enterTransition()
  public fun detachTransition(): ExitTransition = exitTransition()

  public fun popEnterTransition(): EnterTransition = enterTransition()
  public fun popExitTransition(): ExitTransition = exitTransition()

  public fun popAttachTransition(): EnterTransition = attachTransition()
  public fun popDetachTransition(): ExitTransition = detachTransition()

  override fun provideTransitions(
    compositionState: PortalRendererState,
    isForBackstack: Boolean
  ): ComposePortalTransition = when(compositionState) {
    PortalRendererState.Added, PortalRendererState.Removed -> when {
      isForBackstack -> ComposePortalTransition(
        enter = popEnterTransition(),
        exit = popExitTransition()
      )

      else -> ComposePortalTransition(
        enter = enterTransition(),
        exit = exitTransition()
      )
    }

    PortalRendererState.Attached, PortalRendererState.Detached -> when {
      isForBackstack -> ComposePortalTransition(
        enter = popAttachTransition(),
        exit = popDetachTransition()
      )

      else -> ComposePortalTransition(
        enter = attachTransition(),
        exit = detachTransition()
      )
    }
  }
}

public fun interface ComposePortalTransitionsProvider {
  public fun provideTransitions(
    compositionState: PortalRendererState,
    isForBackstack: Boolean
  ): ComposePortalTransition

  public companion object {
    public val None: ComposePortalTransitionsProvider = ComposePortalTransitionsProvider { _, _ ->
      ComposePortalTransition.None
    }

    public val Default: ComposePortalTransitionsProvider =
      ComposePortalTransitionsProvider { compositionState, isForBackstack ->
        when(compositionState) {
          PortalRendererState.Added, PortalRendererState.Removed -> when {
            isForBackstack -> ComposePortalTransition(
              enter = ComposePortalTransition.defaultEnterForBackstack,
              exit = ComposePortalTransition.defaultExitForBackstack
            )

            else -> ComposePortalTransition(
              enter = ComposePortalTransition.defaultEnter,
              exit = ComposePortalTransition.defaultExit
            )
          }

          PortalRendererState.Attached, PortalRendererState.Detached -> when {
            isForBackstack -> ComposePortalTransition(
              enter = ComposePortalTransition.defaultAttachForBackstack,
              exit = ComposePortalTransition.defaultDetachForBackstack
            )

            else -> ComposePortalTransition(
              enter = ComposePortalTransition.defaultAttach,
              exit = ComposePortalTransition.defaultDetach
            )
          }
        }
      }

    public fun provideTransitions(
      state: PortalRendererState,
      isForBackstack: Boolean
    ): ComposePortalTransition = Default.provideTransitions(state, isForBackstack)
  }
}
