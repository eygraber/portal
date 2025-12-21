plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-android-kmp-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
  id("com.eygraber.conventions-publish-maven-central")
}

kotlin {
  defaultKmpTargets(
    project = project,
    androidNamespace = "com.eygraber.portal.compose",
  )

  sourceSets {
    commonMain.dependencies {
      api(projects.portal)
      implementation(libs.compose.animation)
    }

    commonTest.dependencies {
      implementation(kotlin("test-common"))
      implementation(kotlin("test-annotations-common"))
    }
  }
}
