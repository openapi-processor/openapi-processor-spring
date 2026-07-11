@file:Suppress("UnstableApiUsage", "UNUSED_VARIABLE")

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    groovy
    kotlin
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

testing {
    suites {
        getByName<JvmTestSuite>("test") {
            useJUnitJupiter()
        }
    }
}
