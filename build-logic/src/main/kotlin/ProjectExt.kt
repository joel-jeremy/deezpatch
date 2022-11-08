import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.the
import java.io.File

/**
 * Workaround for accessing the version catalog from the build-logic project.
 *
 * See https://github.com/gradle/gradle/issues/15383
 */
val org.gradle.api.Project.libs get() = the<org.gradle.accessors.dm.LibrariesForLibs>()

/**
 * Get java projects.
 */
fun org.gradle.api.Project.javaProjects(): List<Project> {
    return subprojects.filter { File(it.projectDir, "src").exists() }
}
