plugins {
  id("emissary.java-library-conventions")
  id("emissary.java-multi-jvm-test-conventions")
  id("emissary.java-testing-conventions")
  id("emissary.java-code-quality-conventions")
  id("emissary.java-publish-conventions")
  id("emissary.eclipse-conventions")
  alias(libs.plugins.jmh)
}

description = "Emissary Core"

tasks.named<Jar>("jar") {
  manifest {
    attributes(mapOf(
      "Automatic-Module-Name" to "io.github.joeljeremy.emissary.core"
    ))
  }
}

dependencies {
  jmh("org.springframework:spring-context:5.3.39")
  jmh("net.sizovs:pipelinr:0.11")
  jmh("org.greenrobot:eventbus-java:3.3.1")
}

val benchmarksFolderPath = "src/jmh/java/io/github/joeljeremy/emissary/core/benchmarks"

jmh {
  jmhVersion = "1.37"
  humanOutputFile = layout.buildDirectory.file("reports/jmh/human.txt")
  resultsFile = layout.projectDirectory.file("${benchmarksFolderPath}/results-java${JavaVersion.current().majorVersion}.json")
  resultFormat = "JSON"
  jvmArgs.addAll(listOf("-Xmx2G"))
}
