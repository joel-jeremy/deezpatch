plugins { 
  id("com.github.kt3k.coveralls")
}

coveralls {
  sourceDirs = mainSrcDirs()
  jacocoReportPath =
      "${rootProject.buildDir}/reports/jacoco/allCodeCoverageReport/allCodeCoverageReport.xml"
}

tasks.named("coveralls").configure {
  group = "Coverage reports"
  description = "Uploads the aggregated coverage report to Coveralls"

  dependsOn(tasks.withType<JacocoReport>())
}

fun mainSrcDirs(): List<String> {
  return javaProjects()
      .mapNotNull { it.extensions.findByType<JavaPluginExtension>() }
      .flatMap { it.sourceSets.getByName<SourceSet>("main").allSource.srcDirs }
      .map { it.absolutePath }
}
