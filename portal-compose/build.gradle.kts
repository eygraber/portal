import com.eygraber.portal.gradle.portalTargets

plugins {
  id("portal-kotlin-multiplatform")
  id("portal-android-library")
  id("portal-compose-jetbrains")
  id("portal-detekt")
  id("portal-publish")
}

android {
  namespace = "com.eygraber.portal.compose"
}

kotlin {
  explicitApi()

  portalTargets()

  sourceSets {
    commonMain {
      dependencies {
        api(projects.portal)
        implementation(compose.animation)
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
