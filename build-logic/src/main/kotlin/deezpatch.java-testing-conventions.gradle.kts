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
          testTask {
            onlyIf("skipIntegrationTests property is not set.") {
              !project.hasProperty("skipIntegrationTests")
            }
            shouldRunAfter(tasks.named("test"))
          }
        }
      }
    }
    withType<JvmTestSuite>().configureEach {
      useJUnitJupiter(libs.versions.junitjupiter)
    }
  }
}

tasks.named("check") {
  dependsOn(testing.suites.named<JvmTestSuite>("integrationTest"))
}