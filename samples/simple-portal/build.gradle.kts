import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  kotlin("plugin.serialization")
  detekt
}

dependencies {
  implementation(projects.portal)

  implementation(compose.material)
  implementation(compose.materialIconsExtended)
  implementation(compose.desktop.currentOs)

  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.simpleportal.DesktopAppKt"
  }
}
