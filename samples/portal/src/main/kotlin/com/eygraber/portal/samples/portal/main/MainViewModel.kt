package com.eygraber.portal.samples.portal.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eygraber.portal.PortalTransitions
import com.eygraber.portal.Portals
import com.eygraber.portal.samples.portal.VM
import com.eygraber.portal.samples.portal.main.home.HomeView

class MainViewModel(
  private val mainPortals: Portals<MainPortalKey>
) : VM<MainState> {
  private val mutableState = object : MainState {
    override var selectedTab by mutableStateOf(MainPortalKey.One)
  }
  override val state = mutableState

  private val homeView = HomeView()

  init {
    mainPortals.withTransaction {
      add(MainPortalKey.One) {
        homeView.render()
      }

      add(MainPortalKey.Two, isAttachedToComposition = false) {
        Text(
          text = "2",
          modifier = Modifier.fillMaxSize(),
          textAlign = TextAlign.Center
        )
      }

      add(MainPortalKey.Three, isAttachedToComposition = false) {
        Text(
          text = "3",
          modifier = Modifier.fillMaxSize(),
          textAlign = TextAlign.Center
        )
      }

      add(MainPortalKey.Four, isAttachedToComposition = false) {
        Text(
          text = "4",
          modifier = Modifier.fillMaxSize(),
          textAlign = TextAlign.Center
        )
      }
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

    mainPortals.withTransaction {
      val transition = tab.getTransitionOverride(tabMovingFrom)
      detachFromComposition(tabMovingFrom, transition)
      attachToComposition(tab, transition)
    }
  }
}

private fun MainPortalKey.getTransitionOverride(
  currentTab: MainPortalKey
) = currentTab.selectedIndex.let { currentSelectedIndex ->
  if(currentSelectedIndex > selectedIndex) {
    PortalTransitions(
      enter = slideInHorizontally { -it },
      exit = slideOutHorizontally { it * 2 }
    )
  }
  else if(currentSelectedIndex < selectedIndex) {
    PortalTransitions(
      enter = slideInHorizontally { it * 2 },
      exit = slideOutHorizontally { -it }
    )
  }
  else {
    PortalTransitions.None
  }
}

private val MainPortalKey.selectedIndex
  get() = when(this) {
    MainPortalKey.One -> 1
    MainPortalKey.Two -> 2
    MainPortalKey.Three -> 3
    MainPortalKey.Four -> 4
  }
