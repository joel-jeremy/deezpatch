import org.gradle.plugins.ide.eclipse.model.AbstractClasspathEntry
import org.gradle.plugins.ide.eclipse.model.Classpath
import org.gradle.plugins.ide.eclipse.model.SourceFolder

plugins {
  id("eclipse")
}

eclipse {
  classpath.file.whenMerged(Action<Classpath> {
    // To fix an issue in Eclipse buildship where dependent projects cannot resolve classes
    // of the dependee project if dependee has JMH sources.
    entries
        .filterIsInstance<AbstractClasspathEntry>()
        .filter { it.path.startsWith("src/jmh") }
        .forEach { it.entryAttributes["test"] = "true" }

    excludeInfoFiles(this)
  })
}

/**
 * Exclude module-info and package-info when compiling through Eclipse.
 * @see https://github.com/ben-manes/caffeine/blob/master/gradle/plugins/src/main/kotlin/lifecycle/eclipse.caffeine.gradle.kts
 */
fun excludeInfoFiles(classpath: Classpath) {
  classpath.entries.filterIsInstance<SourceFolder>().forEach {
    val excludes = it.excludes.toMutableList()
    excludes += "module-info.java"
    if (it.path != "src/main/java") {
      excludes += "**/package-info.java"
    }
    it.excludes = excludes
  }
}
