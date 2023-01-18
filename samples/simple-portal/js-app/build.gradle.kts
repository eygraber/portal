import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-simple-portal-js"

kotlin {
  kmpTargets(
    project = project,
    js = true,
    isJsLeafModule = true,
    android = false,
    jvm = false,
    ios = false
  )

  sourceSets {
    named("jsMain") {
      dependencies {
        implementation(projects.samples.simplePortal)

        implementation(compose.material)
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
