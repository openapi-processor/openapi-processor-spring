import io.openapiprocessor.build.core.dsl.initFrom
import io.openapiprocessor.build.core.dsl.initSignKey
import io.openapiprocessor.build.core.dsl.sonatype
import io.openapiprocessor.build.core.getPomProperties
import org.gradle.accessors.dm.LibrariesForLibs

plugins {
   id("io.openapiprocessor.build.plugin.publish-central")
}

// see buildSrc/build.gradle.kts
val libs = the<LibrariesForLibs>()

publishing {
    publications {
        create<MavenPublication>("openapiprocessor") {
            from(components["java"])

            pom {
                pom.initFrom(getPomProperties(project))
            }
        }
    }

    repositories {
        sonatype(project)
    }
}

signing {
    initSignKey()
    sign(publishing.publications["openapiprocessor"])
}
