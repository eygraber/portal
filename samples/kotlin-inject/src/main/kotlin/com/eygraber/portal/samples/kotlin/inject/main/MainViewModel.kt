package com.eygraber.portal.samples.kotlin.inject.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.eygraber.portal.PortalManager
import com.eygraber.portal.PortalTransition
import com.eygraber.portal.samples.kotlin.inject.VM
import com.eygraber.portal.samples.kotlin.inject.home.HomePortal
import me.tatarka.inject.annotations.Inject

@MainScope
@Inject
class MainViewModel(
  private val mainBottomNavPortalManager: PortalManager<MainBottomNavPortalKey>,
  private val homePortalProvider: () -> HomePortal
) : VM<MainState> {
  private val mutableState = object : MainState {
    override var selectedTab by mutableStateOf(MainBottomNavPortalKey.Alarm)
  }

  init {
    mainBottomNavPortalManager.withTransaction {
      add(MainBottomNavPortalKey.Alarm, portal = homePortalProvider())

      add(MainBottomNavPortalKey.Two, isAttachedToComposition = false) {
        Text(
          text = "2",
          modifier = Modifier.fillMaxSize(),
          textAlign = TextAlign.Center
        )
      }

      add(MainBottomNavPortalKey.Three, isAttachedToComposition = false) {
        Text(
          text = "3",
          modifier = Modifier.fillMaxSize(),
          textAlign = TextAlign.Center
        )
      }

      add(MainBottomNavPortalKey.Four, isAttachedToComposition = false) {
        Text(
          text = "4",
          modifier = Modifier.fillMaxSize(),
          textAlign = TextAlign.Center
        )
      }
    }
  }

  override val state = mutableState

  fun handle1Clicked() {
    moveToTab(MainBottomNavPortalKey.Alarm)
  }

  fun handle2Clicked() {
    moveToTab(MainBottomNavPortalKey.Two)
  }

  fun handle3Clicked() {
    moveToTab(MainBottomNavPortalKey.Three)
  }

  fun handle4Clicked() {
    moveToTab(MainBottomNavPortalKey.Four)
  }

  private fun moveToTab(tab: MainBottomNavPortalKey) {
    val tabMovingFrom = state.selectedTab
    mutableState.selectedTab = tab

    mainBottomNavPortalManager.withTransaction {
      val transition = tab.getTransitionOverride(tabMovingFrom)
      detachFromComposition(tabMovingFrom, transition.exit)
      attachToComposition(tab, transition.enter)
    }
  }
}

private fun MainBottomNavPortalKey.getTransitionOverride(
  currentTab: MainBottomNavPortalKey
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

private val MainBottomNavPortalKey.selectedIndex
  get() = when(this) {
    MainBottomNavPortalKey.Alarm -> 1
    MainBottomNavPortalKey.Two -> 2
    MainBottomNavPortalKey.Three -> 3
    MainBottomNavPortalKey.Four -> 4
  }
