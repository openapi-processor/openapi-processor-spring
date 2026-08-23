import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    id("io.github.ben-manes.versions")
}

val projectPath = project.path

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf {
//        println("candidate: $candidate stable: ${!candidate.version.isNonStable()}")
        candidate.version.isNonStable()
    }

//    outputFormatter {
//        exceeded.dependencies.removeIf { d -> ignore.contains("${d.group}:${d.name}") }
//
//        val plainTextReporter = PlainTextReporter(
//            projectPath,
//            revision,
//            gradleReleaseChannel
//        )
//        plainTextReporter.write(System.out, this)
//    }
}

fun String.isNonStable(): Boolean {
    val nonStable = listOf(
        ".M[0-9]+$",
        ".RC[0-9]*$",
        ".alpha.?[0-9]+$",
        ".beta.?[0-9]+$",
    )

    for (n in nonStable) {
       if (this.contains("(?i)$n".toRegex())) {
           //println("not stable: $this")
           return true
       }
    }

    return false
}

val ignore = listOf(
    "org.checkerframework:jdk8"
)
