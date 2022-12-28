import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

dependencies {
  implementation(projects.samples.simplePortal)

  implementation(compose.desktop.currentOs)
  implementation(compose.material)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.simpleportal.SimplePortalAppKt"
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
