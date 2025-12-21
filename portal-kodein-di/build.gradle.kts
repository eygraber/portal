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
    androidNamespace = "com.eygraber.portal.kodein.di",
  )

  sourceSets {
    commonMain.dependencies {
      implementation(projects.portal)

      implementation(libs.kodein.core)
    }

    commonTest.dependencies {
      implementation(kotlin("test-common"))
      implementation(kotlin("test-annotations-common"))
    }
  }
}
