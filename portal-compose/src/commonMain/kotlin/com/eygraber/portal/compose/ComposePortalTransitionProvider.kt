package com.eygraber.portal.compose

import com.eygraber.portal.PortalRendererState

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
