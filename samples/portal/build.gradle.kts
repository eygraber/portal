import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

dependencies {
  implementation(projects.portalCompose)
  implementation(projects.portalKodeinDi)

  implementation(compose.material)
  implementation(compose.materialIconsExtended)
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
