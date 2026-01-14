plugins {
  id("emissary.java-conventions")
  id("maven-publish")
  id("signing")
}

// Do not publish test fixtures.
val javaComponent = components.getByName<AdhocComponentWithVariants>("java")
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

publishing {
  publications {
    register<MavenPublication>("mavenJava") {
      from(components["java"])

      pom {
        packaging = "jar"
        name = project.providers.provider { project.description }
        description = project.providers.provider { project.description }
        url = "https://github.com/joel-jeremy/emissary"

        licenses {
          license {
            name = "The Apache License, Version 2.0"
            url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
          }
        }

        developers {
          developer {
            id = "joel-jeremy"
            name = "Joel Jeremy M. Marquez"
            email = "joeljeremy.marquez@gmail.com"
            roles = listOf("owner", "developer")
          }
        }

        scm {
          connection = "scm:git:https://github.com/joel-jeremy/emissary.git"
          developerConnection = "scm:git:https://github.com/joel-jeremy/emissary.git"
          url = "https://github.com/joel-jeremy/emissary"
        }
      }
    }
  }

  repositories {
    maven {
      url = uri("https://maven.pkg.github.com/joel-jeremy/emissary")
      name = "githubPackages"
      credentials {
        username = findProperty("githubActor") as String?
        password = findProperty("githubToken") as String?
      }
    }
  }
}

if (project.hasProperty("release")) {
  // If release property is set, do not allow publishing with SNAPSHOT dependencies.
  configurations.all {
    resolutionStrategy.eachDependency {
      if (requested.version?.endsWith("-SNAPSHOT") == true) {
        throw GradleException("Cannot release with SNAPSHOT dependencies (${requested.module}:${requested.version}).")
      }
    }
  }
}

signing {
  isRequired = hasProperty("signingRequired")

  val signingKey: String? by project
  val signingPassword: String? by project
  useInMemoryPgpKeys(signingKey, signingPassword)
  
  sign(publishing.publications["mavenJava"])
}
