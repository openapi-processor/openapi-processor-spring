import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-library`
    groovy
    kotlin
    jacoco
    alias(libs.plugins.versions)
    alias(libs.plugins.sonar)
    alias(libs.plugins.updates)
    id("openapiprocessor.test")
    id("openapiprocessor.testInt")
    id("openapiprocessor.publish")
    id("jacoco-report-aggregation")
}

group = "io.openapiprocessor"
version = libs.versions.processor.get()
println("version: $version")

versions {
    packageName = "io.openapiprocessor.spring"
    entries.putAll(mapOf(
        "version" to libs.versions.processor.get()
    ))
}

java {
    withJavadocJar ()
    withSourcesJar ()
}

kotlin {
    jvmToolchain(libs.versions.build.jdk.get().toInt())

    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        jvmTarget = JvmTarget.fromTarget(libs.versions.target.jdk.get())
    }
}

tasks.compileTestGroovy {
    classpath += sourceSets.main.get().compileClasspath
    classpath += files(tasks.compileKotlin.get().destinationDirectory)
    classpath += files(tasks.compileTestKotlin.get().destinationDirectory)
}

dependencies {
    implementation (libs.openapi.processor.api)
    implementation (libs.openapi.processor.core)
    implementation (libs.openapi.processor.test.api)
    implementation (libs.openapi.processor.parser.api)
    implementation (libs.openapi.processor.parser.swagger)
    implementation (libs.openapi.processor.parser.openapi4j)
    implementation (libs.slf4j)

    testImplementation (libs.openapi.processor.api)
    testImplementation (libs.openapi.processor.core)
    testImplementation (libs.openapi.processor.test.api)
    testImplementation (libs.openapi.processor.test.base)
    testImplementation (libs.openapi.processor.parser.api)
    testImplementation (libs.openapi.processor.parser.swagger)
    testImplementation (libs.openapi.processor.parser.openapi4j)
    testImplementation (platform(libs.groovy.bom))
    testImplementation ("org.apache.groovy:groovy")
    testImplementation ("org.apache.groovy:groovy-nio")
    testImplementation (libs.spock)
    testImplementation (platform(libs.kotest.bom))
    testImplementation (libs.kotest.runner)
    testImplementation (libs.kotest.table)
    testImplementation (libs.mockk)
    testImplementation (libs.logback)
    testImplementation (libs.jimfs)

    testIntImplementation (libs.openapi.processor.api)
    testIntImplementation (libs.openapi.processor.core)
    testIntImplementation (libs.openapi.processor.test.api)
    testIntImplementation (libs.openapi.processor.test.base)
    testIntImplementation (libs.openapi.processor.parser.api)
    testIntImplementation (libs.openapi.processor.parser.swagger)
    testIntImplementation (libs.openapi.processor.parser.openapi4j)
    testIntImplementation (platform(libs.groovy.bom))
    testIntImplementation ("org.apache.groovy:groovy")
    testIntImplementation ("org.apache.groovy:groovy-nio")
    testIntImplementation (libs.spock)
    testIntImplementation (platform(libs.kotest.bom))
    testIntImplementation (libs.kotest.runner)
    testIntImplementation (libs.kotest.table)
    testIntImplementation (libs.mockk)
    testIntImplementation (libs.logback)
    testIntImplementation (libs.jimfs)
    testIntImplementation (libs.spring.web)
    testIntImplementation (libs.spring.data)
}

//tasks.named("dependencyUpdates").configure {
//    rejectVersionIf {
//        String v = it.candidate.version
//        println "candidate: $v"
//        return v.endsWith ("-M1") || v.contains ("alpha") || v.contains ("beta")
//    }
//}

tasks.withType<Test>().configureEach {
    jvmArgs(listOf(
        "--add-exports", "jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED",
        "--add-exports", "jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED"
    ))

    javaLauncher.set(javaToolchains.launcherFor {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.build.jdk.get()))
    })

    finalizedBy(tasks.named("jacocoTestReport"))
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    executionData.from(tasks.named<Test>("test").map<File> {
        it.extensions.getByType(JacocoTaskExtension::class.java).destinationFile as File
    })
    executionData.from(tasks.named<Test>("testInt").map<File> {
        it.extensions.getByType(JacocoTaskExtension::class.java).destinationFile as File
    })

    reports {
        xml.required = true
        html.required = true
    }
}

sonarqube {
  properties {
    property("sonar.projectKey", "openapi-processor_openapi-processor-spring")
    property("sonar.organization", "openapi-processor")
    property("sonar.host.url", "https://sonarcloud.io")
    property("sonar.coverage.jacoco.xmlReportPaths", layout.buildDirectory.dir("reports/jacoco/test/jacocoTestReport.xml").get().toString())
  }
}
