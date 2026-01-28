package com.eygraber.portal.samples.simpleportal.shared

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.compose.SimplePortalTransitionProvider
import com.eygraber.portal.compose.exitTransitionOverride
import com.eygraber.portal.push
import kotlinx.coroutines.delay

private val portalManager = ComposePortalManager<PortalKey>(
  defaultTransitionProvider = object : SimplePortalTransitionProvider {
    override val enterTransition = slideInHorizontally(
      animationSpec = tween(1000),
      initialOffsetX = { it * 2 },
    )

    override val exitTransition = slideOutHorizontally(
      animationSpec = tween(1000),
      targetOffsetX = { it * 2 },
    )

    override val backstackPushDetachTransition = fadeOut(
      animationSpec = tween(1000),
    )
  },
)

@Composable
fun SimplePortal() {
  LaunchedEffect(Unit) {
    delay(500)

    portalManager.addTenNumberBoxPortals()

    delay(2500)

    portalManager.withTransaction {
      val initialWaveDuration = 3000
      val secondWaveDuration = 6000
      val lastWaveDuration = 8000

      backstack.clear(
        exitTransitionOverride = { key ->
          when(key) {
            PortalKey.One -> exitTransitionOverride {
              fadeOut(
                animationSpec = tween(durationMillis = lastWaveDuration),
              )
            }

            PortalKey.Two -> exitTransitionOverride {
              shrinkOut(
                animationSpec = tween(durationMillis = lastWaveDuration),
              )
            }

            PortalKey.Three -> exitTransitionOverride {
              slideOutVertically(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetY = { -it },
              )
            }

            PortalKey.Four -> exitTransitionOverride {
              slideOutVertically(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetY = { it * 2 },
              )
            }

            PortalKey.Five -> exitTransitionOverride {
              slideOutHorizontally(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetX = { -it },
              )
            }

            PortalKey.Six -> exitTransitionOverride {
              slideOutHorizontally(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetX = { it * 2 },
              )
            }

            PortalKey.Seven -> exitTransitionOverride {
              slideOutVertically(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetY = { -it },
              )
            }

            PortalKey.Eight -> exitTransitionOverride {
              slideOutVertically(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetY = { it * 2 },
              )
            }

            PortalKey.Nine -> exitTransitionOverride {
              slideOutHorizontally(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetX = { -it },
              )
            }

            PortalKey.Ten -> exitTransitionOverride {
              slideOutHorizontally(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetX = { it * 2 },
              )
            }
          }
        },
      )
    }
  }

  MaterialTheme(
    colorScheme = darkColorScheme(
      primary = Color(0xFFBB86FC),
      secondary = Color(0xFF03DAC5),
      tertiary = Color(0xFF3700B3),
    ),
  ) {
    Surface(
      modifier = Modifier.fillMaxSize(),
    ) {
      Box {
        @Suppress("UnnecessaryFullyQualifiedName")
        portalManager.Render()
      }
    }
  }
}

suspend fun ComposePortalManager<PortalKey>.addTenNumberBoxPortals() {
  suspend fun ComposePortalManager<PortalKey>.addNumberBoxPortal(
    previousKey: PortalKey?,
    key: PortalKey,
  ) {
    withTransaction {
      backstack.push(key) {
        if(previousKey != null) detachFromComposition(previousKey)

        add(
          portal = NumberBoxPortal(key),
        )
      }
    }

    delay(1_000L)
  }

  PortalKey
    .entries
    .scan<PortalKey, PortalKey?>(null) { previous, current ->
      addNumberBoxPortal(previousKey = previous, key = current)
      current
    }
}
