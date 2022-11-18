plugins {
  id("deezpatch.java-conventions")
}

additionalTestRunsOnJvmVersions().forEach { javaVersion ->
  val testTaskName = "testOnJava${javaVersion}"

  val testTask = tasks.register<Test>(testTaskName) {
    useJUnitPlatform()

    javaLauncher.set(javaToolchains.launcherFor {
      languageVersion.set(javaVersion)
    })
  }

  tasks.named("check").configure {
    dependsOn(testTask)
  }
}

/**
 * Ideally every LTS release (succeeding the version used in source compilation) 
 * plus the latest released non-LTS version.
 */
fun additionalTestRunsOnJvmVersions(): List<JavaLanguageVersion> {
  val defaultJvmVersions = "17,19"
  val jvmVersions = findProperty("additionalTestRunsOnJvmVersions") as String?
      ?: defaultJvmVersions
  return jvmVersions.split(",").filter { it.isNotEmpty() }.map { JavaLanguageVersion.of(it) }
}
