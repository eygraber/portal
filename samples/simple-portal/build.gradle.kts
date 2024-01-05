import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-simple-portal-shared"

android {
  namespace = "com.eygraber.portal.samples.simpleportal"
}

kotlin {
  defaultKmpTargets(
    project = project,
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
