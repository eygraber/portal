package com.eygraber.portal.samples.simpleportal

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.singleWindowApplication
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.compose.ComposePortalEntry
import com.eygraber.portal.compose.ComposePortalTransition
import com.eygraber.portal.compose.PortalManager
import com.eygraber.portal.push
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.swing.UIManager

enum class PortalKey {
  One,
  Two,
  Three,
  Four,
  Five,
  Six,
  Seven,
  Eight,
  Nine,
  Ten
}

@Composable
fun NumberBox(text: String) {
  Box(modifier = Modifier.fillMaxSize()) {
    Text(text, modifier = Modifier.align(Alignment.Center))
  }
}

fun main() {
  val portalManager = PortalManager<PortalKey>(
    defaultTransitionsProvider = { _, isForBackstack ->
      if(isForBackstack) {
        ComposePortalTransition(
          enter = fadeIn(
            animationSpec = tween(1000)
          ),
          exit = fadeOut(
            animationSpec = tween(1000)
          )
        )
      }
      else {
        ComposePortalTransition(
          enter = slideInHorizontally(
            animationSpec = tween(1000),
            initialOffsetX = { it * 2 }
          ),
          exit = slideOutHorizontally(
            animationSpec = tween(1000),
            targetOffsetX = { it * 2 }
          )
        )
      }
    }
  )

  @OptIn(DelicateCoroutinesApi::class)
  GlobalScope.launch {
    portalManager.updates().collect {
      println(portalManager.size)
    }
  }

  @OptIn(DelicateCoroutinesApi::class)
  GlobalScope.launch {
    delay(500)

    portalManager.addTen()

    delay(2500)

    portalManager.withTransaction {
      val initialWaveDuration = 3000
      val secondWaveDuration = 6000
      val lastWaveDuration = 8000

      backstack.clear(
        suppressTransitions = false,
        exitExtra = { key ->
          when(key) {
            PortalKey.One -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                fadeOut(
                  animationSpec = tween(durationMillis = lastWaveDuration)
                )
              )
            )

            PortalKey.Two -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                shrinkOut(
                  animationSpec = tween(durationMillis = lastWaveDuration)
                )
              )
            )

            PortalKey.Three -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutVertically(
                  animationSpec = tween(durationMillis = secondWaveDuration),
                  targetOffsetY = { -it }
                )
              )
            )

            PortalKey.Four -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutVertically(
                  animationSpec = tween(durationMillis = secondWaveDuration),
                  targetOffsetY = { it * 2 }
                )
              )
            )

            PortalKey.Five -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutHorizontally(
                  animationSpec = tween(durationMillis = secondWaveDuration),
                  targetOffsetX = { -it }
                )
              )
            )

            PortalKey.Six -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutHorizontally(
                  animationSpec = tween(durationMillis = secondWaveDuration),
                  targetOffsetX = { it * 2 }
                )
              )
            )

            PortalKey.Seven -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutVertically(
                  animationSpec = tween(durationMillis = initialWaveDuration),
                  targetOffsetY = { -it }
                )
              )
            )

            PortalKey.Eight -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutVertically(
                  animationSpec = tween(durationMillis = initialWaveDuration),
                  targetOffsetY = { it * 2 }
                )
              )
            )

            PortalKey.Nine -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutHorizontally(
                  animationSpec = tween(durationMillis = initialWaveDuration),
                  targetOffsetX = { -it }
                )
              )
            )

            PortalKey.Ten -> ComposePortalEntry.Extra(
              transitionOverride = ComposePortalTransition.exit(
                slideOutHorizontally(
                  animationSpec = tween(durationMillis = initialWaveDuration),
                  targetOffsetX = { it * 2 }
                )
              )
            )
          }
        }
      )
    }
  }

  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName())
  singleWindowApplication(title = "Simple Portal") {
    MaterialTheme(
      colors = darkColors(
        primary = Color(0xFFBB86FC),
        primaryVariant = Color(0xFF3700B3),
        secondary = Color(0xFF03DAC5)
      )
    ) {
      Surface(
        modifier = Modifier.fillMaxSize()
      ) {
        portalManager.Render()
      }
    }
  }
}

suspend fun PortalManager<PortalKey>.addTen() {
  val addDelay = 1000L

  withTransaction {
    backstack.push(PortalKey.One) {
      add(
        PortalKey.One,
        portal = ComposePortal { NumberBox("1") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Two) {
      detachFromComposition(PortalKey.One)

      add(
        PortalKey.Two,
        portal = ComposePortal { NumberBox("2") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Three) {
      detachFromComposition(PortalKey.Two)

      add(
        PortalKey.Three,
        portal = ComposePortal { NumberBox("3") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Four) {
      detachFromComposition(PortalKey.Three)

      add(
        PortalKey.Four,
        portal = ComposePortal { NumberBox("4") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Five) {
      detachFromComposition(PortalKey.Four)

      add(
        PortalKey.Five,
        portal = ComposePortal { NumberBox("5") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Six) {
      detachFromComposition(PortalKey.Five)

      add(
        PortalKey.Six,
        portal = ComposePortal { NumberBox("6") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Seven) {
      detachFromComposition(PortalKey.Six)

      add(
        PortalKey.Seven,
        portal = ComposePortal { NumberBox("7") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Eight) {
      detachFromComposition(PortalKey.Seven)

      add(
        PortalKey.Eight,
        portal = ComposePortal { NumberBox("8") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Nine) {
      detachFromComposition(PortalKey.Eight)

      add(
        PortalKey.Nine,
        portal = ComposePortal { NumberBox("9") }
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Ten) {
      detachFromComposition(PortalKey.Nine)

      add(
        PortalKey.Ten,
        portal = ComposePortal { NumberBox("10") }
      )
    }
  }
}
