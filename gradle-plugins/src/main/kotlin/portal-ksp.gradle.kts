import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetContainer

val libs = the<LibrariesForLibs>()

plugins {
  id("com.google.devtools.ksp")
}

// https://github.com/google/ksp/issues/37
val kotlin = extensions["kotlin"]
if(kotlin is KotlinProjectExtension) {
  kotlin.configureKsp()
}

fun KotlinSourceSetContainer.configureKsp() {
  runCatching {
    sourceSets.configureEach {
      kotlin.srcDir("build/generated/ksp/$name/kotlin")
    }
  }
}


