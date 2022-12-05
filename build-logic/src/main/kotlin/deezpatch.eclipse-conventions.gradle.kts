import org.gradle.plugins.ide.eclipse.model.AbstractClasspathEntry
import org.gradle.plugins.ide.eclipse.model.Classpath

plugins {
  id("eclipse")
}

eclipse.classpath.file.whenMerged(Action<Classpath> {
  // To fix an issue in Eclipse buildship where dependent projects cannot resolve classes
  // of the dependee project if dependee has JMH sources.
  entries
      .filterIsInstance<AbstractClasspathEntry>()
      .filter { it.path.startsWith("src/jmh") }
      .forEach { it.entryAttributes["test"] = "true" }
})
