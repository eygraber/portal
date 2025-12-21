import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
}

group = "samples-icons"

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.portal.samples.icons",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(libs.compose.ui)
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
