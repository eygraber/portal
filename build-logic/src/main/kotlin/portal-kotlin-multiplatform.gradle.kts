@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
  kotlin("multiplatform")
  id("portal-kotlin-library")
}

afterEvaluate {
  project.extensions.findByType<KotlinMultiplatformExtension>()?.let { kmpExt ->
    kmpExt.sourceSets.removeAll { sourceSet ->
      sourceSet.name == "androidAndroidTestRelease" ||
        sourceSet.name.startsWith("androidTestFixtures")
    }
  }
}


