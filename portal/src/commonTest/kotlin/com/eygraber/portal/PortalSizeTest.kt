package com.eygraber.portal

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import kotlin.test.Test
import io.kotest.matchers.ints.shouldBeExactly

enum class PortalKey {
  Test,
  OtherTest
}

class PortalSizeTest {
  @Test
  fun adding_a_portal_increments_the_size() {
    // asserts size internally
    createPortalManagerWithTestPortal()
  }

  @Test
  fun removing_a_portal_decrements_the_size() {
    val portalManager = createPortalManagerWithTestPortal()

    portalManager.withTransaction {
      remove(PortalKey.Test)
    }

    portalManager.size shouldBeExactly 0
    portalManager.isEmpty.shouldBeTrue()
    portalManager.isNotEmpty.shouldBeFalse()
  }

  @Test
  fun adding_a_portal_to_the_backstack_increments_the_size() {
    // asserts size internally
    createPortalManagerWithTestPortalOnTheBackstack()
  }

  @Test
  fun adding_a_portal_increments_the_size_within_the_transaction() {
    val portalManager = createPortalManagerWithTestPortal()

    portalManager.withTransaction {
      add(object : KeyedPortal<PortalKey> {
        override val key = PortalKey.OtherTest
      })

      size shouldBeExactly 2
    }
  }

  @Test
  fun adding_a_portal_does_not_increment_the_size_outside_of_the_transaction() {
    val portalManager = createPortalManagerWithTestPortal()

    portalManager.withTransaction {
      add(object : KeyedPortal<PortalKey> {
        override val key = PortalKey.OtherTest
      })

      portalManager.size shouldBeExactly 1
    }

    portalManager.size shouldBeExactly 2
  }

  @Test
  fun popping_a_portal_from_the_backstack_decrements_the_size() {
    val portalManager = createPortalManagerWithTestPortalOnTheBackstack()

    portalManager.withTransaction {
      backstack.pop()
    }

    portalManager.size shouldBeExactly 0
    portalManager.isEmpty.shouldBeTrue()
    portalManager.isNotEmpty.shouldBeFalse()
  }

  @Test
  fun removing_a_portal_that_is_in_the_backstack_decrements_the_size() {
    val portalManager = createPortalManagerWithTestPortalOnTheBackstack()

    portalManager.withTransaction {
      remove(PortalKey.Test)
    }

    portalManager.size shouldBeExactly 0
    portalManager.isEmpty.shouldBeTrue()
    portalManager.isNotEmpty.shouldBeFalse()
  }

  private fun createPortalManagerWithTestPortal() = object : PortalManager<PortalKey>() {}.apply {
    withTransaction {
      add(object : KeyedPortal<PortalKey> {
        override val key = PortalKey.Test
      })
    }

    size shouldBeExactly 1
    backstack.size shouldBeExactly 0
    isEmpty.shouldBeFalse()
    isNotEmpty.shouldBeTrue()
  }

  private fun createPortalManagerWithTestPortalOnTheBackstack() = object : PortalManager<PortalKey>() {}.apply {
    withTransaction {
      backstack.push(PortalKey.Test) {
        add(object : KeyedPortal<PortalKey> {
          override val key = PortalKey.Test
        })
      }
    }

    size shouldBeExactly 1
    backstack.size shouldBeExactly 1
    isEmpty.shouldBeFalse()
    isNotEmpty.shouldBeTrue()
  }
}
