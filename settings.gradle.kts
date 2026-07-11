plugins {
    id("com.gradle.develocity").version("4.5.0")
}

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
