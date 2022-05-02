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
import com.eygraber.portal.compose.ComposePortalEntry.ExitExtra.Companion.exitTransitionOverride
import com.eygraber.portal.compose.PortalManager
import com.eygraber.portal.compose.PortalTransition
import com.eygraber.portal.compose.SimplePortalTransitionProvider
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
    defaultTransitionProvider = object : SimplePortalTransitionProvider {
      override fun enterExitTransition() = PortalTransition(
        enter = slideInHorizontally(
          animationSpec = tween(1000),
          initialOffsetX = { it * 2 }
        ),
        exit = slideOutHorizontally(
          animationSpec = tween(1000),
          targetOffsetX = { it * 2 }
        )
      )

      override fun popEnterExitTransition() = PortalTransition(
        enter = fadeIn(
          animationSpec = tween(1000)
        ),
        exit = fadeOut(
          animationSpec = tween(1000)
        )
      )
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
            PortalKey.One -> exitTransitionOverride(
              fadeOut(
                animationSpec = tween(durationMillis = lastWaveDuration)
              )
            )

            PortalKey.Two -> exitTransitionOverride(
              shrinkOut(
                animationSpec = tween(durationMillis = lastWaveDuration)
              )
            )

            PortalKey.Three -> exitTransitionOverride(
              slideOutVertically(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetY = { -it }
              )
            )

            PortalKey.Four -> exitTransitionOverride(
              slideOutVertically(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetY = { it * 2 }
              )
            )

            PortalKey.Five -> exitTransitionOverride(
              slideOutHorizontally(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetX = { -it }
              )
            )

            PortalKey.Six -> exitTransitionOverride(
              slideOutHorizontally(
                animationSpec = tween(durationMillis = secondWaveDuration),
                targetOffsetX = { it * 2 }
              )
            )

            PortalKey.Seven -> exitTransitionOverride(
              slideOutVertically(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetY = { -it }
              )
            )

            PortalKey.Eight -> exitTransitionOverride(
              slideOutVertically(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetY = { it * 2 }
              )
            )

            PortalKey.Nine -> exitTransitionOverride(
              slideOutHorizontally(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetX = { -it }
              )
            )

            PortalKey.Ten -> exitTransitionOverride(
              slideOutHorizontally(
                animationSpec = tween(durationMillis = initialWaveDuration),
                targetOffsetX = { it * 2 }
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

class NumberBoxPortal(
  private val number: String
) : ComposePortal {
  @Composable
  override fun Render() {
    NumberBox(number)
  }
}

suspend fun PortalManager<PortalKey>.addTen() {
  val addDelay = 1000L

  withTransaction {
    backstack.push(PortalKey.One) {
      add(
        PortalKey.One,
        portal = NumberBoxPortal("1")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Two) {
      detachFromComposition(PortalKey.One)

      add(
        PortalKey.Two,
        portal = NumberBoxPortal("2")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Three) {
      detachFromComposition(PortalKey.Two)

      add(
        PortalKey.Three,
        portal = NumberBoxPortal("3")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Four) {
      detachFromComposition(PortalKey.Three)

      add(
        PortalKey.Four,
        portal = NumberBoxPortal("4")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Five) {
      detachFromComposition(PortalKey.Four)

      add(
        PortalKey.Five,
        portal = NumberBoxPortal("5")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Six) {
      detachFromComposition(PortalKey.Five)

      add(
        PortalKey.Six,
        portal = NumberBoxPortal("6")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Seven) {
      detachFromComposition(PortalKey.Six)

      add(
        PortalKey.Seven,
        portal = NumberBoxPortal("7")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Eight) {
      detachFromComposition(PortalKey.Seven)

      add(
        PortalKey.Eight,
        portal = NumberBoxPortal("8")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Nine) {
      detachFromComposition(PortalKey.Eight)

      add(
        PortalKey.Nine,
        portal = NumberBoxPortal("9")
      )
    }
  }

  delay(addDelay)

  withTransaction {
    backstack.push(PortalKey.Ten) {
      detachFromComposition(PortalKey.Nine)

      add(
        PortalKey.Ten,
        portal = NumberBoxPortal("10")
      )
    }
  }
}
