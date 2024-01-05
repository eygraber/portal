import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-icons"

android {
  namespace = "com.eygraber.portal.samples.icons"
}

kotlin {
  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    js = true,
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(compose.ui)
      }
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
