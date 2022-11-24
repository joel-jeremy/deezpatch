plugins { 
  id("test-report-aggregation")
  id("jacoco-report-aggregation")
}

reporting {
  reports {
    register<JacocoCoverageReport>("allCodeCoverageReport") { 
      testType.set("all")
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
    register<AggregateTestReport>("testAggregateTestReport") { 
      testType.set(TestSuiteType.UNIT_TEST)
    }
    register<AggregateTestReport>("integrationTestAggregateTestReport") { 
      testType.set(TestSuiteType.INTEGRATION_TEST)
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
