dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "emissary"

includeBuild("build-logic")

include("emissary-core")
