package com.eygraber.portal.samples.simpleportal.android

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.eygraber.portal.samples.simpleportal.SimplePortal
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
// needed until Robolectric supports 34
@Config(
  sdk = [33],
)
class SimplePortalAndroidTest {
  @get:Rule val rule = createComposeRule()

  @Before
  fun setup() {
    ShadowLog.stream = System.out
  }

  @Test
  fun testSimplePortal() {
    val mainClock = rule.mainClock

    rule.setContent {
      SimplePortal()
    }

    mainClock.autoAdvance = false

    // delay before adding 1
    mainClock.advanceTimeBy(500)

    mainClock.advanceTimeByFrame() // trigger recomposition after 1 is added

    for(i in 1..10) {
      rule.assertPositionThroughTransition(i)
    }

    mainClock.advanceTimeByFrame()
    mainClock.advanceTimeByFrame()
    rule.onNodeWithText("9").assertDoesNotExist()

    mainClock.advanceTimeBy(13_000 - mainClock.currentTime)
    mainClock.advanceTimeByFrame()
    rule.waitForIdle()
    mainClock.advanceTimeByFrame()

    for(i in 1..10) {
      rule
        .onNodeWithText(i.toString())
        .assertIsDisplayed()
        .assertLeftPositionInRootIsEqualTo(159.dp)
    }

    mainClock.advanceTimeBy(14_000 - mainClock.currentTime)

    rule
      .onNodeWithText("10")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo(430.dp)

    rule
      .onNodeWithText("9")
      .assertIsDisplayed()
      .assertLeftPositionInRootIsEqualTo(23.dp)

    rule
      .onNodeWithText("8")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo(159.dp)
      .assertTopPositionInRootIsEqualTo(614.dp)

    rule
      .onNodeWithText("7")
      .assertIsDisplayed()
      .assertLeftPositionInRootIsEqualTo(159.dp)
      .assertTopPositionInRootIsEqualTo(16.dp)

    mainClock.advanceTimeBy(16_500 - mainClock.currentTime)

    for(i in 7..10) {
      rule
        .onNodeWithText(i.toString())
        .assertDoesNotExist()
    }

    rule
      .onNodeWithText("6")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo(709.dp)

    rule
      .onNodeWithText("5")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo((-116).dp)

    rule
      .onNodeWithText("4")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo(159.dp)
      .assertTopPositionInRootIsEqualTo(1022.dp)

    rule
      .onNodeWithText("3")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo(159.dp)
      .assertTopPositionInRootIsEqualTo((-189).dp)

    mainClock.advanceTimeBy(19_500 - mainClock.currentTime)

    for(i in 3..6) {
      rule
        .onNodeWithText(i.toString())
        .assertDoesNotExist()
    }

    rule
      .onNodeWithText("2")
      .assertExists()
      .assertIsNotDisplayed()
      .assertLeftPositionInRootIsEqualTo(2.dp)
      .assertTopPositionInRootIsEqualTo((-15).dp)

    rule
      .onNodeWithText("1")
      .assertIsDisplayed()
      .assertLeftPositionInRootIsEqualTo(159.dp)
      .assertTopPositionInRootIsEqualTo(215.dp)

    mainClock.advanceTimeBy(21_500 - mainClock.currentTime)

    for(i in 1..2) {
      rule
        .onNodeWithText(i.toString())
        .assertDoesNotExist()
    }
  }
}

private fun ComposeTestRule.assertPositionThroughTransition(
  value: Int,
) {
  val strValue = value.toString()
  waitForIdle() // layout pass needed to setup transition
  mainClock.advanceTimeByFrame() // give animation a start time

  onNodeWithText(strValue).assertExists().assertIsNotDisplayed()

  // apprx 60% of the way through the transition
  mainClock.advanceTimeBy(value * 1000 + 100 - mainClock.currentTime)

  onNodeWithText(strValue)
    .assertIsDisplayed()
    .assertLeftPositionInRootIsEqualTo(
      if(value == 1) 260.dp else 251.dp,
    )

  // apprx 100% of the way through the transition
  mainClock.advanceTimeBy(value * 1000 + 500 - mainClock.currentTime)

  onNodeWithText(strValue)
    .assertIsDisplayed()
    .assertLeftPositionInRootIsEqualTo(
      if(value == 1) 160.dp else 159.dp,
    )

  if(value - 2 > 0) {
    onNodeWithText((value - 2).toString()).assertDoesNotExist()
  }
}
