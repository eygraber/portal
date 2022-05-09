import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

val libs = the<LibrariesForLibs>()

plugins {
  id("com.google.devtools.ksp")
}

// https://github.com/google/ksp/issues/37
val kotlin = extensions["kotlin"]
if(kotlin is KotlinMultiplatformExtension) {
  kotlin.targets.configureEach {
    kotlin.configureKsp(name)
  }
}

fun KotlinSourceSetContainer.configureKsp(targetName: String) {
  runCatching {
    sourceSets.configureEach {
      kotlin.srcDir("build/generated/ksp/$targetName/$name/kotlin")
    }
  }
}
