import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-ark-js"

kotlin {
  kmpTargets(
    project = project,
    js = true,
    binaryType = BinaryType.Executable,
    android = false,
    jvm = false,
    ios = false
  )

  sourceSets {
    jsMain {
      dependencies {
        implementation(projects.portalCompose)
        implementation(projects.samples.ark.shared)
      }
    }
  }
}

compose.experimental {
  web.application {}
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
