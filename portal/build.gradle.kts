plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
  alias(libs.plugins.atomicfu)
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.portal",
  )

  js {
    browser {
      testTask {
        enabled = false
      }
    }
  }

  sourceSets {
    commonMain.dependencies {
      implementation(libs.kotlinx.atomicFu)

      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.kotlinx.serialization.json)
    }

    commonTest.dependencies {
      implementation(kotlin("test"))

      implementation(libs.test.kotest.assertions)
    }
  }
}
