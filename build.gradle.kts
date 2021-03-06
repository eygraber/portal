buildscript {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }

  dependencies {
    classpath(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    classpath(libs.buildscript.android)
    classpath(libs.buildscript.compose)
    classpath(libs.buildscript.kotlin)
    classpath(libs.buildscript.atomicFu)
  }

}

plugins {
  id("validate-gradle-properties")
}

tasks.register<Delete>("clean") {
  delete(buildDir)
}
