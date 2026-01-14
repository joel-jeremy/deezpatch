plugins {
  base
  id("emissary.nexus-publish-conventions")
  id("emissary.java-reporting-conventions")
  id("emissary.sonar-conventions")
  id("emissary.coveralls-conventions")
  id("emissary.snyk-conventions")
  id("emissary.dependency-updates-conventions")
  id("emissary.eclipse-conventions")
  id("emissary.idea-conventions")
}

allprojects {
  group = "io.github.joel-jeremy.emissary"

  val snapshotSuffix = if (rootProject.hasProperty("release")) ""  else "-SNAPSHOT"
  version = "1.0.0${snapshotSuffix}"
}
