plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  detekt
  publish
  portal
}

kotlin {
  explicitApi()

  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(projects.portal)
        implementation(compose.animation)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}
