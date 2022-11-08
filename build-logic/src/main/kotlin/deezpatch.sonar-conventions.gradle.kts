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
    property("sonar.coverage.jacoco.xmlReportPaths", 
        "${rootProject.buildDir}/reports/jacoco/allCodeCoverageReport/allCodeCoverageReport.xml")
  }
}

tasks.named("sonar").configure {
  dependsOn(tasks.withType<JacocoReport>())
}

tasks.named("sonarqube").configure {
  dependsOn(tasks.withType<JacocoReport>())
}
