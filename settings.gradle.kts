dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots")
            mavenContent {
                snapshotsOnly()
            }
        }
    }
    versionCatalogs {
        create("build") {
            from(files("./gradle/build.versions.toml"))
        }
    }
}

plugins {
    id("io.github.ben-manes.versions.settings") version("0.61.0")
    id("com.gradle.develocity").version("4.5.0")
}

develocity {
    if (System.getenv("CI") != null) {
        buildScan {
            buildScan {
                termsOfUseUrl.set("https://gradle.com/help/legal-terms-of-use")
                termsOfUseAgree.set("yes")
            }
        }
    }
}

rootProject.name = "openapi-processor-spring"

//includeBuild("../openapi-processor-base")
