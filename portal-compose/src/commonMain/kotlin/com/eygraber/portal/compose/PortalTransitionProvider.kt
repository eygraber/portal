package com.eygraber.portal.compose

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
  public fun enterExitTransition(): PortalTransition

  public fun attachDetachTransition(): PortalTransition = enterExitTransition()

  public fun popEnterExitTransition(): PortalTransition = enterExitTransition()

  public fun popAttachDetachTransition(): PortalTransition = popEnterExitTransition()

  override fun provideTransitions(
    compositionState: PortalRendererState,
    isForBackstack: Boolean
  ): PortalTransition = when(compositionState) {
    PortalRendererState.Added, PortalRendererState.Removed -> when {
      isForBackstack -> popEnterExitTransition()

      else -> enterExitTransition()
    }

    PortalRendererState.Attached, PortalRendererState.Detached -> when {
      isForBackstack -> popAttachDetachTransition()

      else -> attachDetachTransition()
    }
  }
}
