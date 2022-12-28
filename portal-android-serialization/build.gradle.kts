plugins {
  id("kotlin-android")
  id("com.eygraber.conventions-kotlin-library")
  id("com.eygraber.conventions-android-library")
  id("com.eygraber.conventions-detekt")
  id("com.eygraber.conventions-publish-maven-central")
}

android {
  namespace = "com.eygraber.portal.android.serialization"
}

dependencies {
  implementation(projects.portal)
}
