import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("portal-kotlin-library")
  id("org.jetbrains.compose")
  id("portal-detekt")
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
