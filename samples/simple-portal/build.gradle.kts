import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("portal-kotlin-library")
  id("org.jetbrains.compose")
  id("portal-detekt")
}

dependencies {
  implementation(projects.portalCompose)

  implementation(compose.material)
  implementation(compose.materialIconsExtended)
  implementation(compose.desktop.currentOs)

  implementation(libs.kotlinx.coroutines.core)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.simpleportal.SimplePortalAppKt"
  }
}
