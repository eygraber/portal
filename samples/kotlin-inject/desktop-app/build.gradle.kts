import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  kotlin("jvm")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

dependencies {
  implementation(projects.portalCompose)
  implementation(projects.samples.kotlinInject.shared)
  implementation(compose.desktop.currentOs)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.kotlin.inject.KotlinInjectPortalAppKt"
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
