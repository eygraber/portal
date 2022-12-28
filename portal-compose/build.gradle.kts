plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.portal.compose"
}

kotlin {
  kmpTargets(
    project = project,
    android = true,
    jvm = true,
    js = true
  )

  sourceSets {
    commonMain {
      dependencies {
        api(projects.portal)
        implementation(compose.animation)
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
