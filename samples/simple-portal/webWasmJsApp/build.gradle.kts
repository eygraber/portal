import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt2")
}

group = "samples-simple-portal-web-wasmjs"

kotlin {
  kmpTargets(
    KmpTarget.WasmJs,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      moduleName = "simple-portal-wasm",
    ),
    ignoreDefaultTargets = true,
  )

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        outputFileName = "simple-portal-wasm.js"
        experiments += "topLevelAwait"
      }
    }
  }

  sourceSets {
    wasmJsMain {
      dependencies {
        implementation(projects.samples.simplePortal.shared)

        implementation(libs.compose.ui)
      }
    }
  }
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
