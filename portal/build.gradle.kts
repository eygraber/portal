plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  id("kotlinx-atomicfu")
}

android {
  namespace = "com.eygraber.portal"
}

kotlin {
  defaultKmpTargets(
    project = project,
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(libs.kotlinx.atomicFu)

        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)
      }
    }

    // commonTest {
    //   dependencies {
    //     implementation(kotlin("test"))
    //
    //     implementation(libs.test.kotest.assertions)
    //   }
    // }
  }
}
