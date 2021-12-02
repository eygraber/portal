import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val libs = the<LibrariesForLibs>()

repositories {
  google()
  mavenCentral()
}

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    allWarningsAsErrors = true
    jvmTarget = libs.versions.jdk.get()
    sourceCompatibility = libs.versions.jdk.get()
    targetCompatibility = libs.versions.jdk.get()
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
  }
}

plugins.withId("org.jetbrains.kotlin.multiplatform") {
  with(extensions.getByType<KotlinMultiplatformExtension>()) {
    sourceSets.configureEach {
      languageSettings.optIn("kotlin.RequiresOptIn")
    }

    jvm().compilations.all {
      kotlinOptions {
        jvmTarget = libs.versions.jdk.get()
        compileKotlinTask.sourceCompatibility = libs.versions.jdk.get()
        compileKotlinTask.targetCompatibility = libs.versions.jdk.get()
      }
    }

    targets.all {
      compilations.all {
        kotlinOptions {
          allWarningsAsErrors = true
          freeCompilerArgs = emptyList()
        }
      }
    }
  }
}
