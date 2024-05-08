import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.android.application")
  kotlin("android")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-simple-portal-android"

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  namespace = "com.eygraber.portal.samples.simpleportal.android"

  defaultConfig {
    applicationId = "com.eygraber.portal.samples.simpleportal.android"
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
  implementation(projects.samples.simplePortal)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)

  testImplementation(libs.test.espresso)
  testImplementation(libs.test.compose.android.uiJunit)
  implementation(libs.test.compose.android.uiTestManifest)
  testImplementation(libs.test.junit)
  testImplementation(libs.test.robolectric)
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
