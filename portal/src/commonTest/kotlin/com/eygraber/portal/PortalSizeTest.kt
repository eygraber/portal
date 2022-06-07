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
  fun `Adding a portal increments the size`() {
    // asserts size internally
    createPortalManagerWithTestPortal()
  }

  @Test
  fun `Removing a portal decrements the size`() {
    val portalManager = createPortalManagerWithTestPortal()

    portalManager.withTransaction {
      remove(PortalKey.Test)
    }

    portalManager.size shouldBeExactly 0
    portalManager.isEmpty.shouldBeTrue()
    portalManager.isNotEmpty.shouldBeFalse()
  }

  @Test
  fun `Adding a portal to the backstack increments the size`() {
    // asserts size internally
    createPortalManagerWithTestPortalOnTheBackstack()
  }

  @Test
  fun `Adding a portal increments the size within the transaction`() {
    val portalManager = createPortalManagerWithTestPortal()

    portalManager.withTransaction {
      add(object : KeyedPortal<PortalKey> {
        override val key = PortalKey.OtherTest
      })

      size shouldBeExactly 2
    }
  }

  @Test
  fun `Adding a portal does not increment the size outside of the transaction`() {
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
  fun `Popping a portal from the backstack decrements the size`() {
    val portalManager = createPortalManagerWithTestPortalOnTheBackstack()

    portalManager.withTransaction {
      backstack.pop()
    }

    portalManager.size shouldBeExactly 0
    portalManager.isEmpty.shouldBeTrue()
    portalManager.isNotEmpty.shouldBeFalse()
  }

  @Test
  fun `Removing a portal that is in the backstack decrements the size`() {
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
