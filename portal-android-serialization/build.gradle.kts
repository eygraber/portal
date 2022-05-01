plugins {
  id("portal-android-library")
  id("kotlin-android")
  id("portal-kotlin-library")
  id("portal-detekt")
  id("portal-publish")
}

dependencies {
  implementation(projects.portal)
}
