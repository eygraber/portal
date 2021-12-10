plugins {
  kotlin("multiplatform")
  id("org.jetbrains.compose")
  id("kotlinx-atomicfu")
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
        api(libs.kotlinx.atomicFu)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)

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
