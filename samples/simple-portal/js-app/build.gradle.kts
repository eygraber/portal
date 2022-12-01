import com.eygraber.portal.gradle.portalTargets

plugins {
  id("portal-kotlin-multiplatform")
  id("portal-compose-jetbrains")
  id("portal-detekt")
}

kotlin {
  portalTargets(
    js = true,
    isJsLeafModule = true,
    android = false,
    jvm = false,
    ios = false
  )

  sourceSets {
    named("jsMain") {
      dependencies {
        implementation(projects.samples.simplePortal)

        implementation(compose.material)
      }
    }
  }
}

compose.experimental {
  web.application {}
}
