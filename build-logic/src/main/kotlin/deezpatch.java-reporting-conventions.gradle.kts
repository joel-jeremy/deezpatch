plugins { 
  id("test-report-aggregation")
  id("jacoco-report-aggregation")
}

reporting {
  reports {
    val allCodeCoverageReport by creating(JacocoCoverageReport::class) {
      testSuiteName = "all"
      reportTask {
        val testTasks = javaProjects()
            .flatMap { it.tasks.withType<Test>() }

        val execFiles = javaProjects()
            .flatMap { it.tasks.withType<JacocoReport>() }
            .flatMap { it.executionData.files }

        dependsOn(testTasks)
        executionData(execFiles)
      }
    }
    val testAggregateTestReport by creating(AggregateTestReport::class) { 
      testSuiteName = "test"
    }
    val integrationTestAggregateTestReport by creating(AggregateTestReport::class) { 
      testSuiteName = "integrationTest"
    }
  }
}

tasks.register("reports") {
  dependsOn(reporting.reports.withType<JacocoCoverageReport>().map { it.reportTask })
  dependsOn(reporting.reports.withType<AggregateTestReport>().map { it.reportTask })
}

javaProjects().forEach {
  it.tasks.withType<Test>().configureEach {
    finalizedBy(tasks.withType<JacocoReport>())
  }
}

dependencies {
  javaProjects().forEach {
    testReportAggregation(project(it.path))
    jacocoAggregation(project(it.path))
  }
}
