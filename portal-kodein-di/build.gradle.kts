import com.eygraber.portal.gradle.portalTargets

plugins {
  id("portal-kotlin-multiplatform")
  id("portal-android-library")
  id("portal-detekt")
  id("portal-publish")
  id("kotlinx-atomicfu")
}

android {
  namespace = "com.eygraber.portal.kodein.di"
}

kotlin {
  explicitApi()

  portalTargets()

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.portal)

        implementation(libs.kodein.core)
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}
