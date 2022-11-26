plugins {
  base
  id("deezpatch.nexus-publish-conventions")
  id("deezpatch.java-reporting-conventions")
  id("deezpatch.sonar-conventions")
  id("deezpatch.coveralls-conventions")
  id("deezpatch.snyk-conventions")
  id("deezpatch.dependency-updates-conventions")
  id("deezpatch.eclipse-conventions")
  id("deezpatch.idea-conventions")
}

allprojects {
  group = "io.github.joel-jeremy.deezpatch"

  val snapshotSuffix = if (rootProject.hasProperty("release")) ""  else "-SNAPSHOT"
  version = "1.0.0-beta.1${snapshotSuffix}"
}
