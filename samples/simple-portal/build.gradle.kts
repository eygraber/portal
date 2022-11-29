import com.eygraber.portal.gradle.portalTargets
import org.jetbrains.compose.compose

plugins {
  id("portal-kotlin-multiplatform")
  id("portal-android-library")
  id("portal-compose-jetbrains")
  id("portal-detekt")
}

android {
  namespace = "com.eygraber.portal.samples.simpleportal"
}

kotlin {
  portalTargets()

  sourceSets.commonMain {
    dependencies {
      implementation(projects.portalCompose)

      implementation(compose.material)

      implementation(libs.kotlinx.coroutines.core)
    }
  }
}
