plugins {
  id("deezpatch.java-library-conventions")
  id("deezpatch.java-testing-conventions")
  id("deezpatch.java-code-quality-conventions")
  id("deezpatch.java-publish-conventions")
  id("deezpatch.java-multi-jvm-test-conventions")
  // See https://youtrack.jetbrains.com/issue/KTIJ-19370
  @Suppress("DSL_SCOPE_VIOLATION")
  alias(libs.plugins.jmh)
}

description = "Deezpatch Core"

tasks.named<Jar>("jar").configure {
  manifest {
    attributes(mapOf(
      "Automatic-Module-Name" to "io.github.joeljeremy.deezpatch.core"
    ))
  }
}

dependencies {
  jmh("org.springframework:spring-context:5.3.23")
  jmh("net.sizovs:pipelinr:0.7")
  jmh("org.greenrobot:eventbus-java:3.3.1")
}

jmh {
  jmhVersion.set("1.35")
  humanOutputFile.set(project.file("${project.buildDir}/reports/jmh/human.txt"))
  resultsFile.set(project.file("${project.buildDir}/reports/jmh/results.json"))
  resultFormat.set("JSON")
  jvmArgs.addAll(listOf("-Xmx2G"))
}
