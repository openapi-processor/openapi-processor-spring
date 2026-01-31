import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    jacoco
    id("org.barfuin.gradle.jacocolog")
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.named<JacocoReport>("jacocoTestReport") {
    reports {
        xml.required.set(true)
        csv.required.set(false)
        html.required.set(false)
        //html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}
