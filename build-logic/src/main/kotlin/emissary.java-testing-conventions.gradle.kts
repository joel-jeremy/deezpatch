plugins {
  id("emissary.java-conventions")
  id("java-test-fixtures")
  id("jvm-test-suite")
}

testing {
  suites {
    register<JvmTestSuite>("integrationTest") {
      targets {
        all {
          testTask {
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