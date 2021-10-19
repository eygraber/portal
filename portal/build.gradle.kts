plugins {
  kotlin("multiplatform")
  kotlin("plugin.serialization")
  id("org.jetbrains.compose")
  id("kotlinx-atomicfu")
  detekt
  publish
}

kotlin {
  explicitApi()

  jvm()

  sourceSets {
    val commonMain by getting {
      dependencies {
        api(libs.kotlinx.atomicFu)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization)

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
