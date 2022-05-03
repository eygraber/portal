import org.jetbrains.compose.compose

plugins {
  id("com.android.application")
  kotlin("android")
  id("portal-kotlin-library")
  id("org.jetbrains.compose")
  id("portal-detekt")
}

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
      "Typos"
    )
  }

  compileOptions {
    sourceCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
    targetCompatibility = JavaVersion.toVersion(libs.versions.jdk.get())
  }
}

dependencies {
  implementation(projects.samples.simplePortal)

  implementation(compose.material)
  implementation(compose.materialIconsExtended)

  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.core)
}
