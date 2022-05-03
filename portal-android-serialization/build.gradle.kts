plugins {
  id("portal-android-library")
  id("kotlin-android")
  id("portal-kotlin-library")
  id("portal-detekt")
  id("portal-publish")
}

android {
  namespace = "com.eygraber.portal.android.serialization"
}

dependencies {
  implementation(projects.portal)
}
