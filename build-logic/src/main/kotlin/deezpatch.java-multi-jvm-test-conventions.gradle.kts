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

    if (javaVersion.canCompileOrRun(17)) {
      // We are reflectively setting environment variable in some unit tests.
      // As of Java 17, this is no longer permitted. We need this flag to re-enable
      // the unit test hack. We are opening java.util because we are reflectively
      // accessing the internal mutable map of a Collections.unmodifiableMap instance.
      jvmArgs.add("--add-opens=java.base/java.util=ALL-UNNAMED")
    }
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
