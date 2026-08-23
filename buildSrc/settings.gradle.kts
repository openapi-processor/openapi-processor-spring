@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("build") {
            from(files("../gradle/build.versions.toml"))
        }
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }

    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
}
