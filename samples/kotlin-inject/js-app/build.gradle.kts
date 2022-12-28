import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

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
        implementation(projects.portalCompose)
        implementation(projects.samples.kotlinInject.shared)

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
