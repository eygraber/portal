import com.eygraber.portal.gradle.portalTargets

plugins {
  id("portal-kotlin-multiplatform")
  id("portal-detekt")
  id("portal-publish")
  id("kotlinx-atomicfu")
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
