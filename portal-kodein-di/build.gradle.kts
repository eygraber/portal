plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
  id("kotlinx-atomicfu")
}

android {
  namespace = "com.eygraber.portal.kodein.di"
}

kotlin {
  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    js = true,
  )

  sourceSets {
    commonMain {
      dependencies {
        implementation(projects.portal)

        implementation(libs.kodein.core)
      }
    }

    commonTest {
      dependencies {
        implementation(kotlin("test-common"))
        implementation(kotlin("test-annotations-common"))
      }
    }
  }
}
