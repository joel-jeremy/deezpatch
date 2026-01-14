plugins {
  id("org.sonarqube")
}

sonar {
  properties {
    val sonarToken = findProperty("sonarToken")
    if (sonarToken != null) {
      property("sonar.token", sonarToken)
    }
    property("sonar.projectName", rootProject.name)
    property("sonar.projectKey", rootProject.group)
    property("sonar.organization", "joel-jeremy")
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.coverage.jacoco.xmlReportPaths", 
      file(rootProject.layout.buildDirectory.file("reports/jacoco/allCodeCoverageReport/allCodeCoverageReport.xml")))
  }
}

val allCodeCoverageReport by tasks.existing
tasks.named("sonar") {
  dependsOn(tasks.withType<JacocoReport>())
  inputs.files(allCodeCoverageReport.map { it.outputs.files })
}
