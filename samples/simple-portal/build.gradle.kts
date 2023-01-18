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
  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    js = true
  )

  sourceSets.commonMain {
    dependencies {
      implementation(projects.portalCompose)

      implementation(compose.material)

      implementation(libs.kotlinx.coroutines.core)
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
