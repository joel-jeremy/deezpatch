dependencyResolutionManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral()
  }
}

rootProject.name = "deezpatch"

includeBuild("build-logic")

include("deezpatch-core")
