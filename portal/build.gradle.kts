import com.eygraber.portal.gradle.portalTargets

plugins {
  id("portal-kotlin-multiplatform")
  id("portal-android-library")
  id("portal-detekt")
  id("portal-publish")
  id("kotlinx-atomicfu")
}

android {
  namespace = "com.eygraber.portal"
}

kotlin {
  explicitApi()

  portalTargets()

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.atomicFu)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test"))

        implementation(libs.test.kotest.assertions)
      }
    }
  }
}
