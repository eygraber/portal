plugins {
  `kotlin-dsl`
}

repositories {
  google()
  mavenCentral()
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.buildscript.agp)
  implementation(libs.buildscript.atomicFu)
  implementation(libs.buildscript.compose)
  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.dokka)
  implementation(libs.buildscript.kotlin)
  implementation(libs.buildscript.publish)
  implementation(libs.buildscript.serialization)
}
