import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-portal"

dependencies {
  implementation(projects.portalCompose)
  implementation(projects.portalKodeinDi)
  implementation(projects.samples.icons)

  implementation(compose.material3)
  implementation(compose.desktop.currentOs)

  implementation(libs.kodein.core)

  implementation(libs.kotlinx.coroutines.core)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.portal.PortalAppKt"
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
