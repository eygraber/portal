import org.gradle.api.GradleException
import org.gradle.api.tasks.PathSensitivity

tasks.register("validateGradleProperties") {
  val wrapperProperties = arrayOf(
    rootProject.file("gradle/wrapper/gradle-wrapper.properties"),
    rootProject.file("gradle-plugins/gradle/wrapper/gradle-wrapper.properties")
  )

  val properties = arrayOf(
    rootProject.file("gradle.properties"),
    rootProject.file("gradle-plugins/gradle.properties")
  )

  inputs.files(wrapperProperties).withPathSensitivity(PathSensitivity.RELATIVE)
  inputs.files(properties).withPathSensitivity(PathSensitivity.RELATIVE)

  outputs.file(
    layout.buildDirectory.file("reports/portal/gradle-properties-match")
  )

  doFirst {
    val wrapperPropertiesMatch = wrapperProperties.mapTo(HashSet(wrapperProperties.size)) { it.readText() }.size == 1
    val propertiesMatch = properties.mapTo(HashSet(properties.size)) { it.readText() }.size == 1
    if(wrapperPropertiesMatch && propertiesMatch) {
      outputs.files.first().writeText("1")
    }
    else {
      outputs.files.first().delete()
      throw GradleException(
        """|Gradle properties don't match
           |  wrapperPropertiesMatch = $wrapperPropertiesMatch
           |  propertiesMatch = $propertiesMatch""".trimMargin()
      )
    }
  }
}
