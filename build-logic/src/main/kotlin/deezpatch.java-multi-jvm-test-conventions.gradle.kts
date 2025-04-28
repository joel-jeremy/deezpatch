plugins {
  id("deezpatch.java-conventions")
}

additionalTestRunsOnJvmVersions().forEach { additionalJavaVersion ->
  val testTaskName = "testOnJava${additionalJavaVersion}"

  val testTask = tasks.register<Test>(testTaskName) {
    useJUnitPlatform()
    javaLauncher = javaToolchains.launcherFor {
      languageVersion = additionalJavaVersion
    }
  }

  tasks.named("check") {
    dependsOn(testTask)
  }
}

/**
 * Ideally every LTS release (succeeding the version used in source compilation) 
 * plus the latest released non-LTS version.
 */
fun additionalTestRunsOnJvmVersions(): List<JavaLanguageVersion> {
  // 17 is enabled by default (Default java-conventions toolchain is 17)
  val defaultJvmVersions = "11,21"
  val jvmVersions = findProperty("additionalTestRunsOnJvmVersions") as String?
      ?: defaultJvmVersions
  return jvmVersions.split(",").filter { it.isNotEmpty() }.map { JavaLanguageVersion.of(it) }
}
