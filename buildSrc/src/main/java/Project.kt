import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.register
import java.io.File

val RepositoryHandler.repository get() = run { google(); mavenCentral() }

fun Project.clearProject(file: File) = tasks.register("type", Delete::class) { delete(file) }














