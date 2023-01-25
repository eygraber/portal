import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.google.devtools.ksp")
}

group = "samples-kotlin-inject-shared"

android {
  namespace = "com.eygraber.portal.samples.kotlin.inject.shared"
}

kotlin {
  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    js = true
  )

  commonMainKspDependencies {
    ksp(libs.kotlinInject.compiler)
  }

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.portalCompose)

        implementation(compose.material)
        implementation(compose.materialIconsExtended)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.datetime)
        implementation(libs.kotlinInject.runtime)
      }
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
