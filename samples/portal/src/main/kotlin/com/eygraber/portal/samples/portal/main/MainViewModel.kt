package com.eygraber.portal.samples.portal.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eygraber.portal.compose.ComposePortal
import com.eygraber.portal.compose.ComposePortalManager
import com.eygraber.portal.compose.PortalTransition
import com.eygraber.portal.compose.enterTransitionOverride
import com.eygraber.portal.compose.exitTransitionOverride
import com.eygraber.portal.samples.portal.VM
import com.eygraber.portal.samples.portal.home.HomePortal

class MainViewModel(
  private val mainPortalManager: ComposePortalManager<MainPortalKey>,
  private val homePortalProvider: () -> HomePortal
) : VM<MainState> {
  private val mutableState = object : MainState {
    override var selectedTab by mutableStateOf(MainPortalKey.One)
  }
  override val state = mutableState

  init {
    mainPortalManager.withTransaction {
      add(MainPortalKey.One, portal = homePortalProvider())

      add(
        MainPortalKey.Two,
        isAttachedToComposition = false,
        portal = object : ComposePortal {
          @Composable
          override fun Render() {
            Text(
              text = "2",
              modifier = Modifier.fillMaxSize(),
              textAlign = TextAlign.Center
            )
          }
        }
      )

      add(
        MainPortalKey.Three,
        isAttachedToComposition = false,
        portal = object : ComposePortal {
          @Composable
          override fun Render() {
            Text(
              text = "3",
              modifier = Modifier.fillMaxSize(),
              textAlign = TextAlign.Center
            )
          }
        }
      )

      add(
        MainPortalKey.Four,
        isAttachedToComposition = false,
        portal = object : ComposePortal {
          @Composable
          override fun Render() {
            Text(
              text = "4",
              modifier = Modifier.fillMaxSize(),
              textAlign = TextAlign.Center
            )
          }
        }
      )
    }
  }

  fun handle1Clicked() {
    moveToTab(MainPortalKey.One)
  }

  fun handle2Clicked() {
    moveToTab(MainPortalKey.Two)
  }

  fun handle3Clicked() {
    moveToTab(MainPortalKey.Three)
  }

  fun handle4Clicked() {
    moveToTab(MainPortalKey.Four)
  }

  private fun moveToTab(tab: MainPortalKey) {
    val tabMovingFrom = state.selectedTab
    mutableState.selectedTab = tab

    mainPortalManager.withTransaction {
      val transition = tab.getTransitionOverride(tabMovingFrom)
      detachFromComposition(
        tabMovingFrom,
        exitTransitionOverride {
          transition.exit
        }
      )

      attachToComposition(
        tab,
        enterTransitionOverride {
          transition.enter
        }
      )
    }
  }
}

private fun MainPortalKey.getTransitionOverride(
  currentTab: MainPortalKey
) = currentTab.selectedIndex.let { currentSelectedIndex ->
  if(currentSelectedIndex > selectedIndex) {
    PortalTransition(
      enter = slideInHorizontally { -it },
      exit = slideOutHorizontally { it * 2 }
    )
  }
  else if(currentSelectedIndex < selectedIndex) {
    PortalTransition(
      enter = slideInHorizontally { it * 2 },
      exit = slideOutHorizontally { -it }
    )
  }
  else {
    PortalTransition.None
  }
}

private val MainPortalKey.selectedIndex
  get() = when(this) {
    MainPortalKey.One -> 1
    MainPortalKey.Two -> 2
    MainPortalKey.Three -> 3
    MainPortalKey.Four -> 4
  }
