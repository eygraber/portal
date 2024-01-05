@file:Suppress("ktlint:standard:value-argument-comment")

package com.eygraber.portal.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.eygraber.portal.PortalBackstackState
import com.eygraber.portal.PortalRendererState

public fun interface PortalTransitionProvider {
  public fun provideTransitions(
    compositionState: PortalRendererState,
    backstackState: PortalBackstackState,
  ): PortalTransition

  public companion object {
    public val None: PortalTransitionProvider = PortalTransitionProvider { _, _ ->
      PortalTransition.None
    }

    public val Default: PortalTransitionProvider =
      PortalTransitionProvider { compositionState, backstackState ->
        when(compositionState) {
          PortalRendererState.Added, PortalRendererState.Removed -> when(backstackState) {
            PortalBackstackState.None -> PortalTransition(
              enter = PortalTransition.defaultEnter,
              exit = PortalTransition.defaultExit,
            )

            PortalBackstackState.Pushing -> PortalTransition(
              enter = PortalTransition.defaultBackstackPushEnter,
              exit = ExitTransition.None, // not a valid state
            )

            PortalBackstackState.Popping -> PortalTransition(
              enter = EnterTransition.None, // not a valid state
              exit = PortalTransition.defaultBackstackPopExit,
            )
          }

          PortalRendererState.Attached, PortalRendererState.Detached -> when(backstackState) {
            PortalBackstackState.None -> PortalTransition(
              enter = PortalTransition.defaultAttach,
              exit = PortalTransition.defaultDetach,
            )

            PortalBackstackState.Pushing -> PortalTransition(
              enter = PortalTransition.defaultBackstackPushAttach,
              exit = PortalTransition.defaultBackstackPushDetach,
            )

            PortalBackstackState.Popping -> PortalTransition(
              enter = PortalTransition.defaultBackstackPopAttach,
              exit = PortalTransition.defaultBackstackPopDetach,
            )
          }
        }
      }

    public fun provideTransitions(
      state: PortalRendererState,
      backstackState: PortalBackstackState,
    ): PortalTransition = Default.provideTransitions(state, backstackState)
  }
}

public interface SimplePortalTransitionProvider : PortalTransitionProvider {
  public val enterTransition: EnterTransition
  public val exitTransition: ExitTransition

  public val attachTransition: EnterTransition get() = enterTransition
  public val detachTransition: ExitTransition get() = exitTransition

  public val backstackPushEnterTransition: EnterTransition get() = enterTransition
  public val backstackPopExitTransition: ExitTransition get() = exitTransition

  public val backstackPushAttachTransition: EnterTransition get() = attachTransition
  public val backstackPushDetachTransition: ExitTransition get() = detachTransition

  public val backstackPopAttachTransition: EnterTransition get() = attachTransition
  public val backstackPopDetachTransition: ExitTransition get() = detachTransition

  override fun provideTransitions(
    compositionState: PortalRendererState,
    backstackState: PortalBackstackState,
  ): PortalTransition = when(compositionState) {
    PortalRendererState.Added, PortalRendererState.Removed -> when(backstackState) {
      PortalBackstackState.None -> PortalTransition(
        enter = enterTransition,
        exit = exitTransition,
      )

      PortalBackstackState.Pushing -> PortalTransition(
        enter = backstackPushEnterTransition,
        exit = ExitTransition.None, // not a valid state
      )

      PortalBackstackState.Popping -> PortalTransition(
        enter = EnterTransition.None, // not a valid state
        exit = backstackPopExitTransition,
      )
    }

    PortalRendererState.Attached, PortalRendererState.Detached -> when(backstackState) {
      PortalBackstackState.None -> PortalTransition(
        enter = attachTransition,
        exit = detachTransition,
      )

      PortalBackstackState.Pushing -> PortalTransition(
        enter = backstackPushAttachTransition,
        exit = backstackPushDetachTransition,
      )

      PortalBackstackState.Popping -> PortalTransition(
        enter = backstackPopAttachTransition,
        exit = backstackPopDetachTransition,
      )
    }
  }
}
