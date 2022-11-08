plugins {
  id("deezpatch.java-conventions")
  id("java-test-fixtures")
  id("jvm-test-suite")
}

testing {
  suites {
    register<JvmTestSuite>("integrationTest") {
      testType.set(TestSuiteType.INTEGRATION_TEST)
      targets {
        all {
          testTask.configure {
            shouldRunAfter(tasks.named("test"))
          }
        }
      }
    }
    withType<JvmTestSuite>().configureEach {
      useJUnitJupiter(libs.versions.junitjupiter.get())
    }
  }
}