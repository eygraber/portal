import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  detekt
  portal
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
