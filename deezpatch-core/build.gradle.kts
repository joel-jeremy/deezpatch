plugins {
  id("deezpatch.java-library-conventions")
  id("deezpatch.java-multi-jvm-test-conventions")
  id("deezpatch.java-testing-conventions")
  id("deezpatch.java-code-quality-conventions")
  id("deezpatch.java-publish-conventions")
  id("deezpatch.eclipse-conventions")
  alias(libs.plugins.jmh)
}

description = "Deezpatch Core"

tasks.named<Jar>("jar") {
  manifest {
    attributes(mapOf(
      "Automatic-Module-Name" to "io.github.joeljeremy.deezpatch.core"
    ))
  }
}

dependencies {
  jmh("org.springframework:spring-context:6.2.10")
  jmh("net.sizovs:pipelinr:0.11")
  jmh("org.greenrobot:eventbus-java:3.3.1")
}

val benchmarksFolderPath = "src/jmh/java/io/github/joeljeremy/deezpatch/core/benchmarks"

jmh {
  jmhVersion = "1.37"
  humanOutputFile = layout.buildDirectory.file("reports/jmh/human.txt")
  resultsFile = layout.projectDirectory.file("${benchmarksFolderPath}/results-java${JavaVersion.current().majorVersion}.json")
  resultFormat = "JSON"
  jvmArgs.addAll(listOf("-Xmx2G"))
}
