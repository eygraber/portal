package com.eygraber.portal.gradle

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.kotlin.dsl.named
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal fun org.gradle.api.Project.kotlin(configure: Action<KotlinProjectExtension>) =
  (this as org.gradle.api.plugins.ExtensionAware).extensions.configure("kotlin", configure)
