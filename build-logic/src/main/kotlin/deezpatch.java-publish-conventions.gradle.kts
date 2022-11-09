plugins {
  id("deezpatch.java-conventions")
  id("maven-publish")
  id("signing")
}

// Do not publish test fixtures.
val javaComponent = components["java"] as AdhocComponentWithVariants
javaComponent.withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
javaComponent.withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }

publishing {
  publications {
    register<MavenPublication>("mavenJava") {
      from(components["java"])

      pom {
        name.set(project.description)
        description.set(project.description)
        packaging = "jar"
        url.set("https://github.com/joel-jeremy/deezpatch")

        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }

        developers {
          developer {
            id.set("joel-jeremy")
            name.set("Joel Jeremy M. Marquez")
            email.set("joeljeremy.marquez@gmail.com")
            roles.set(listOf("owner", "developer"))
          }
        }

        scm {
          connection.set("scm:git:https://github.com/joel-jeremy/deezpatch.git")
          developerConnection.set("scm:git:https://github.com/joel-jeremy/deezpatch.git")
          url.set("https://github.com/joel-jeremy/deezpatch")
        }
      }
    }
  }

  repositories {
    maven {
      url = uri("https://maven.pkg.github.com/joel-jeremy/deezpatch")
      name = "github-packages"
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
  // isRequired = false

  val signingKey: String? by project
  val signingPassword: String? by project
  useInMemoryPgpKeys(signingKey, signingPassword)
  
  sign(publishing.publications["mavenJava"])
}
