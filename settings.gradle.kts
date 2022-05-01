@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
  }

  includeBuild("gradle-plugins")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
    maven("https://jitpack.io")
  }
}

rootProject.name = "portals"

include(":portal")
include(":portal-android-serialization")
include(":portal-compose")
include(":portal-kodein-di")

include(":samples:kotlin-inject")
include(":samples:portal")
include(":samples:simple-portal")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
