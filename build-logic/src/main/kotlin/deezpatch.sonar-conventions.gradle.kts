plugins {
  id("org.sonarqube")
}

sonarqube {
  properties {
    val sonarToken = findProperty("sonarToken")
    if (sonarToken != null) {
      property("sonar.login", sonarToken)
    }
    property("sonar.projectName", rootProject.name)
    property("sonar.projectKey", rootProject.group)
    property("sonar.organization", "joel-jeremy")
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.coverage.jacoco.xmlReportPaths", rootProject.layout.buildDirectory.file(
        "reports/jacoco/allCodeCoverageReport/allCodeCoverageReport.xml"))
  }
}

tasks.named("sonar") {
  dependsOn(tasks.withType<JacocoReport>())
}

tasks.named("sonarqube") {
  dependsOn(tasks.withType<JacocoReport>())
}
