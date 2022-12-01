plugins {
  id("com.android.application")
  kotlin("android")
  id("portal-kotlin-library")
  id("portal-compose-jetpack")
  id("portal-detekt")
}

android {
  compileSdk = libs.versions.android.sdk.compile.get().toInt()

  namespace = "com.eygraber.portal.samples.simpleportal.android"

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.composeAndroid.compiler.get()
  }

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
      "Typos"
    )
  }

  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
  }

  testOptions {
    unitTests {
      isIncludeAndroidResources = true
    }
  }
}

dependencies {
  implementation(projects.samples.simplePortal)

  implementation(libs.composeAndroid.material)
  implementation(libs.composeAndroid.materialIconsExtended)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)

  testImplementation(libs.test.espresso)
  testImplementation(libs.test.compose.android.uiJunit)
  implementation(libs.test.compose.android.uiTestManifest)
  testImplementation(libs.test.junit)
  testImplementation(libs.test.robolectric)
}
