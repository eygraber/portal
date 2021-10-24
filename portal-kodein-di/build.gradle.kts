plugins {
  kotlin("multiplatform")
  detekt
  publish
}

kotlin {
  explicitApi()

  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(projects.portal)

        api(libs.kodein.core)
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
