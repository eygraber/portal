import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-kotlin-inject-js"

kotlin {
  kmpTargets(
    KmpTarget.Js,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      moduleName = "kotlin-inject",
    ),
    ignoreDefaultTargets = true,
  )

  sourceSets {
    commonJsMain {
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
