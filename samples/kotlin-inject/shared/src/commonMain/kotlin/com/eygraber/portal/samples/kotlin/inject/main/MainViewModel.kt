package com.eygraber.portal.samples.kotlin.inject.main

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
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
import com.eygraber.portal.samples.kotlin.inject.VM
import com.eygraber.portal.samples.kotlin.inject.home.HomePortal
import me.tatarka.inject.annotations.Inject

@MainScope
@Inject
class MainViewModel(
  private val mainBottomNavPortalManager: ComposePortalManager<MainBottomNavPortalKey>,
  private val homePortalProvider: () -> HomePortal,
) : VM<MainState> {
  private val mutableState = object : MainState {
    override var selectedTab by mutableStateOf(MainBottomNavPortalKey.Alarm)
  }

  init {
    mainBottomNavPortalManager.withTransaction {
      add(portal = homePortalProvider())

      add(
        isAttachedToComposition = false,
        portal = object : ComposePortal<MainBottomNavPortalKey> {
          override val key = MainBottomNavPortalKey.Two
          @Composable
          override fun Render() {
            Text(
              text = "2",
              modifier = Modifier.fillMaxSize(),
              textAlign = TextAlign.Center,
            )
          }
        },
      )

      add(
        isAttachedToComposition = false,
        portal = object : ComposePortal<MainBottomNavPortalKey> {
          override val key = MainBottomNavPortalKey.Three
          @Composable
          override fun Render() {
            Text(
              text = "3",
              modifier = Modifier.fillMaxSize(),
              textAlign = TextAlign.Center,
            )
          }
        },
      )

      add(
        isAttachedToComposition = false,
        portal = object : ComposePortal<MainBottomNavPortalKey> {
          override val key = MainBottomNavPortalKey.Four
          @Composable
          override fun Render() {
            Text(
              text = "4",
              modifier = Modifier.fillMaxSize(),
              textAlign = TextAlign.Center,
            )
          }
        },
      )
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
      detachFromComposition(
        tabMovingFrom,
        exitTransitionOverride {
          transition.exit
        },
      )
      attachToComposition(
        tab,
        enterTransitionOverride {
          transition.enter
        },
      )
    }
  }
}

private fun MainBottomNavPortalKey.getTransitionOverride(
  currentTab: MainBottomNavPortalKey,
) = currentTab.selectedIndex.let { currentSelectedIndex ->
  if(currentSelectedIndex > selectedIndex) {
    PortalTransition(
      enter = slideInHorizontally { -it },
      exit = slideOutHorizontally { it * 2 },
    )
  }
  else if(currentSelectedIndex < selectedIndex) {
    PortalTransition(
      enter = slideInHorizontally { it * 2 },
      exit = slideOutHorizontally { -it },
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
