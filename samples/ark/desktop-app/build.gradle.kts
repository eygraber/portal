import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-ark-desktop"

dependencies {
  implementation(projects.portalCompose)
  implementation(projects.samples.ark.shared)
  implementation(compose.desktop.currentOs)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.ark.ArkPortalAppKt"
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
