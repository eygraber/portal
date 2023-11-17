import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.google.devtools.ksp")
}

group = "samples-ark-shared"

android {
  namespace = "com.eygraber.portal.samples.ark.shared"
}

kotlin {
  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    js = true
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.portalCompose)

        implementation(compose.foundation)

        @OptIn(ExperimentalComposeLibrary::class)
        implementation(compose.material3)

        implementation(libs.kotlinx.coroutines.core)
      }
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
