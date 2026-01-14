import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
  id("com.github.ben-manes.versions")
}

fun isNonStable(version: String): Boolean {
  val nonStableKeyword = listOf("PREVIEW", "ALPHA", "BETA", "SNAPSHOT").any { 
    keyword -> version.uppercase().contains(keyword)
  }
  return nonStableKeyword
}

tasks.withType<DependencyUpdatesTask>().configureEach {
  rejectVersionIf {
    isNonStable(candidate.version) && !isNonStable(currentVersion)
  }
  checkForGradleUpdate = true
  outputFormatter = "html"
}
