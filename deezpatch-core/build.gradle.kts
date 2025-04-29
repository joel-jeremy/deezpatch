plugins {
  id("deezpatch.java-library-conventions")
  id("deezpatch.java-multi-jvm-test-conventions")
  id("deezpatch.java-testing-conventions")
  id("deezpatch.java-code-quality-conventions")
  id("deezpatch.java-publish-conventions")
  id("deezpatch.eclipse-conventions")
  // See https://youtrack.jetbrains.com/issue/KTIJ-19370
  @Suppress("DSL_SCOPE_VIOLATION")
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
  jmh("org.springframework:spring-context:5.3.39")
  jmh("net.sizovs:pipelinr:0.9")
  jmh("org.greenrobot:eventbus-java:3.3.1")
}

jmh {
  jmhVersion = "1.35"
  humanOutputFile = layout.buildDirectory.file("reports/jmh/human.txt")
  resultsFile = layout.buildDirectory.file("reports/jmh/results.json")
  resultFormat = "JSON"
  jvmArgs.addAll(listOf("-Xmx2G"))
}
