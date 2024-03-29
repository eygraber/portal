import com.eygraber.conventions.repositories.addCommonRepositories

pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("androidx.*")
      }
    }

    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") {
      content {
        includeGroupByRegex("org\\.jetbrains.*")
      }
    }

    mavenCentral()

    maven("https://oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    maven("https://s01.oss.sonatype.org/content/repositories/snapshots") {
      mavenContent {
        snapshotsOnly()
      }
    }

    gradlePluginPortal()
  }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
  // comment this out for now because it doesn't work with KMP js
  // https://youtrack.jetbrains.com/issue/KT-51379/
  // repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

  repositories {
    addCommonRepositories(
      includeMavenCentral = true,
      includeMavenCentralSnapshots = true,
      includeGoogle = true,
      includeJetbrainsCompose = true,
    )
  }
}

plugins {
  id("com.eygraber.conventions.settings") version "0.0.70"
  id("com.gradle.enterprise") version "3.16.2"
}

rootProject.name = "portals"

include(":portal")
include(":portal-android-serialization")
include(":portal-compose")
include(":portal-kodein-di")

include(":samples:icons")
include(":samples:kotlin-inject:android-app")
include(":samples:kotlin-inject:desktop-app")
include(":samples:kotlin-inject:shared")
include(":samples:kotlin-inject:webApp")
include(":samples:portal")
include(":samples:simple-portal")
include(":samples:simple-portal:android-app")
include(":samples:simple-portal:desktop-app")
include(":samples:simple-portal:webApp")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

gradleEnterprise {
  buildScan {
    termsOfServiceUrl = "https://gradle.com/terms-of-service"
    if(System.getenv("CI") != null) {
      termsOfServiceAgree = "yes"
      publishAlways()
    }
  }
}
