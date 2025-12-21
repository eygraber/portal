import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
  id("com.google.devtools.ksp")
}

group = "samples-kotlin-inject-shared"

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.portal.samples.kotlin.inject.shared",
  )

  commonMainKspDependencies(project) {
    ksp(libs.kotlinInject.compiler)
  }

  sourceSets {
    commonMain.dependencies {
      implementation(projects.portalCompose)
      implementation(projects.samples.icons)

      implementation(libs.compose.material3)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.datetime)
      implementation(libs.kotlinInject.runtime)
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
