import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
  id("com.eygraber.conventions-kotlin-multiplatform")
  id("com.eygraber.conventions-compose-jetbrains")
  id("com.eygraber.conventions-detekt")
}

group = "samples-simple-portal-web"

afterEvaluate {
  project.tasks.named<Copy>("wasmJsBrowserProductionExecutableDistributeResources").configure {
    dependsOn("wasmJsProductionExecutableCompileSync")
    dependsOn("jsProductionExecutableCompileSync")
  }

  project.tasks.named<Task>("jsBrowserProductionWebpack").configure {
    dependsOn("wasmJsProductionExecutableCompileSync")
  }
}

kotlin {
  kmpTargets(
    KmpTarget.Js,
    KmpTarget.WasmJs,
    project = project,
    binaryType = BinaryType.Executable,
    webOptions = KmpTarget.WebOptions(
      moduleName = "simple-portal",
    ),
    ignoreDefaultTargets = true,
  )

  js(IR) {
    browser {
      commonWebpackConfig {
        outputFileName = "simple-portal.js"
        experiments += "topLevelAwait"
      }
    }
  }

  @OptIn(ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      commonWebpackConfig {
        experiments += "topLevelAwait"
      }
    }
  }

  sourceSets {
    commonJsMain {
      dependencies {
        implementation(projects.samples.simplePortal)

        implementation(compose.ui)
      }
    }
  }
}

compose.experimental {
  web.application {}
}

gradleConventions.kotlin {
  explicitApiMode = ExplicitApiMode.Disabled
}
