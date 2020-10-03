registerPublishTask("snapshot") { hasSnapshotVersion() }
registerPublishTask("release") { !hasSnapshotVersion() }

fun registerPublishTask(type: String, condition: () -> Boolean) {
    tasks.register("publish${type.capitalize()}") {
        group = "publishing"
        description = "Publish only if the current version is a ${type.capitalize()} version"

        if (condition()) {
            println("enabling $type publishing")
            dependsOn(tasks.withType<PublishToMavenRepository>())
        } else {
            doLast {
                println("skipping - no $type version")
            }
        }
    }
}

fun hasSnapshotVersion(): Boolean {
    return version.toString().endsWith("-SNAPSHOT")
}
