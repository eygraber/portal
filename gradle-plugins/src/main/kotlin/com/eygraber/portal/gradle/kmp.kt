package com.eygraber.portal.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

fun KotlinMultiplatformExtension.portalTargets(
  jvm: Boolean = true,
  android: Boolean = false,
  ios: Boolean = false
) {
  if(jvm) {
    jvm()
  }

  if(android) {
    android {
      publishAllLibraryVariants()
    }
  }

  if(ios) {
    ios()
  }
}

/**
 * Provides the existing [androidMain][KotlinSourceSet] element.
 */
val NamedDomainObjectContainer<KotlinSourceSet>.androidMain
  get() = named<KotlinSourceSet>("commonMain")
