import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-kotlin-inject-android"

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  namespace = "com.eygraber.portal.samples.kotlin.inject.android"

  defaultConfig {
    applicationId = "com.eygraber.portal.samples.kotlin.inject.android"
    minSdk = libs.versions.android.sdk.min.get().toInt()
    targetSdk = libs.versions.android.sdk.target.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }

  lint {
    checkDependencies = true
    checkReleaseBuilds = false
    disable += listOf(
      "ParcelCreator",
      "DalvikOverride",
      "InvalidPackage",
      "VulnerableCordovaVersion",
      "MissingTranslation",
      "Typos",
    )
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  implementation(projects.portalCompose)
  implementation(projects.samples.kotlinInject.shared)

  implementation(libs.androidx.activity.compose)
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
