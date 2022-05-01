@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  `kotlin-dsl`
}

tasks.withType<JavaCompile> {
  sourceCompatibility = libs.versions.jdk.get()
  targetCompatibility = libs.versions.jdk.get()
}

kotlin {
  jvmToolchain {
    require(this is JavaToolchainSpec)
    languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
    vendor.set(JvmVendorSpec.AZUL)
  }
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = libs.versions.jdk.get()

    freeCompilerArgs = freeCompilerArgs + listOf(
      "-Xopt-in=kotlin.RequiresOptIn"
    )
  }
}

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
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
  implementation(libs.buildscript.android)
  implementation(libs.buildscript.androidCacheFix)
  implementation(libs.buildscript.atomicFu)
  implementation(libs.buildscript.detekt)
  implementation(libs.buildscript.dokka)
  implementation(libs.buildscript.kotlin)
  implementation(libs.buildscript.ksp)
  implementation(libs.buildscript.publish)
}
