import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  id("portal-detekt")
  id("portal-ksp-multiplatform")
}

dependencies {
  implementation(projects.portalCompose)

  implementation(compose.material)
  implementation(compose.materialIconsExtended)
  implementation(compose.desktop.currentOs)

  implementation(libs.kotlinx.coroutines.core)
  ksp(libs.kotlinInject.compiler)
  implementation(libs.kotlinInject.runtime)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.kotlin.inject.KotlinInjectPortalAppKt"
  }
}
