import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
}

group = "samples-simple-portal-shared"

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.portal.samples.simpleportal.shared",
  )

  sourceSets.commonMain {
    dependencies {
      implementation(projects.portalCompose)

      implementation(compose.material3)

      implementation(libs.kotlinx.coroutines.core)
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
