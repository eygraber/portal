import com.eygraber.portal.gradle.portalTargets

plugins {
  id("portal-kotlin-multiplatform")
  id("org.jetbrains.compose")
  id("portal-detekt")
  id("portal-publish")
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
