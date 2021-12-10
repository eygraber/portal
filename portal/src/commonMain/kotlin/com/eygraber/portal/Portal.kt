package com.eygraber.portal

import androidx.compose.runtime.Composable

public fun interface Portal {
  @Composable
  public fun render()
}

public interface ParentPortal : Portal {
  public val portalManagers: List<PortalManager<*>>
}

public interface ChildPortal : Portal {
  public val parent: Portal
}

public fun interface PortalTransitionsProvider {
  public fun provideTransitions(
    compositionState: PortalCompositionState,
    isForBackstack: Boolean
  ): PortalTransition

  public companion object {
    public val None: PortalTransitionsProvider = PortalTransitionsProvider { _, _ ->
      PortalTransition.None
    }

    public val Default: PortalTransitionsProvider =
      PortalTransitionsProvider { compositionState, isForBackstack ->
        when(compositionState) {
          PortalCompositionState.Added, PortalCompositionState.Removed -> when {
            isForBackstack -> PortalTransition(
              enter = PortalTransition.defaultEnterForBackstack,
              exit = PortalTransition.defaultExitForBackstack
            )

            else -> PortalTransition(
              enter = PortalTransition.defaultEnter,
              exit = PortalTransition.defaultExit
            )
          }

          PortalCompositionState.Attached, PortalCompositionState.Detached -> when {
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
      state: PortalCompositionState,
      isForBackstack: Boolean
    ): PortalTransition = Default.provideTransitions(state, isForBackstack)
  }
}

public interface SaveablePortal {
  public fun saveState()
}
