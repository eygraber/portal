import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.accessors.dm.LibrariesForLibs

val libs = the<LibrariesForLibs>()

plugins {
  id("io.gitlab.arturbosch.detekt")
}

detekt {
  toolVersion = libs.versions.detekt.get()

  source.from("build.gradle.kts")

  autoCorrect = true
  parallel = true

  buildUponDefaultConfig = true

  config = project.files("${project.rootDir}/detekt.yml")
}

tasks.withType<Detekt>().configureEach {
  // Target version of the generated JVM bytecode. It is used for type resolution.
  jvmTarget = libs.versions.jdk.get()
}

dependencies {
  detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${libs.versions.detekt.get()}")
  detektPlugins("com.eygraber.detekt.rules:formatting:${libs.versions.detektEygraber.get()}")
  detektPlugins("com.eygraber.detekt.rules:style:${libs.versions.detektEygraber.get()}")
}
