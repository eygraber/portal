import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
}

group = "samples-simple-portal-web-js"

kotlin {
  kmpTargets(
    KmpTarget.Js,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      moduleName = "simple-portal-js",
    ),
    ignoreDefaultTargets = true,
  )

  js(IR) {
    browser {
      commonWebpackConfig {
        outputFileName = "simple-portal-js.js"
        experiments += "topLevelAwait"
      }
    }
  }

  sourceSets {
    jsMain {
      dependencies {
        implementation(projects.samples.simplePortal.shared)

        implementation(compose.ui)
      }
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
