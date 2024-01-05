import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-kotlin-inject-js"

kotlin {
  kmpTargets(
    project = project,
    js = true,
    binaryType = BinaryType.Executable,
    jsModuleName = "kotlin-inject",
    android = false,
    jvm = false,
    ios = false,
  )

  sourceSets {
    named("jsMain") {
      dependencies {
        implementation(projects.portalCompose)
        implementation(projects.samples.kotlinInject.shared)

        implementation(compose.ui)
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
