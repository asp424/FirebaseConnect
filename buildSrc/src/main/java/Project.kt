import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.artifacts.repositories.MavenArtifactRepository
import org.gradle.api.tasks.Delete
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import java.io.File

val RepositoryHandler.repository: MavenArtifactRepository
get() = run {
    google(); mavenCentral(); maven(
    url = "https://github.com/jitsi/jitsi-maven-repository/raw/master/releases"
)
}

fun Project.clearProject(file: File) = tasks.register("type", Delete::class) { delete(file) }














