@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    kotlin
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

testing {
    suites {
        val test = getByName<JvmTestSuite>("test")

        register<JvmTestSuite>("testInt") {
            useJUnitJupiter()

            dependencies {
                implementation(project())
            }

            sources {
                java {
                    srcDirs("src/testInt/kotlin")
                }
            }

            targets {
                all {
                    testTask.configure {
                        shouldRunAfter(test)
                    }
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("testInt"))
}
