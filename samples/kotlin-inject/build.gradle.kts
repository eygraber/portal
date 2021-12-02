import org.jetbrains.compose.compose

plugins {
  kotlin("jvm")
  id("org.jetbrains.compose")
  detekt
  id("com.google.devtools.ksp") version libs.versions.ksp.get()
}

repositories {
  maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
  implementation(projects.portal)

  implementation(compose.material)
  implementation(compose.materialIconsExtended)
  implementation(compose.desktop.currentOs)

  implementation(libs.kotlinx.coroutines.core)

  ksp(libs.kotlinInject.compiler)
  implementation(libs.kotlinInject.runtime)
}

compose.desktop {
  application {
    mainClass = "com.eygraber.portal.samples.kotlin.inject.KotlinInjectPortalAppKt"
  }
}

// https://github.com/google/ksp/issues/37
sourceSets {
  main {
    java {
      srcDir(layout.buildDirectory.dir("generated/ksp/main/kotlin"))
    }
  }

  test {
    java {
      srcDir(layout.buildDirectory.dir("generated/ksp/test/kotlin"))
    }
  }
}
