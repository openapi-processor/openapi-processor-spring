plugins {
    `kotlin-dsl`
}

dependencies {
    // catalog hack: https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))

    implementation(libs.plugin.kotlin)
//    implementation(libs.plugin.checker)
//    implementation(libs.plugin.updates)
    implementation(libs.plugin.build)
}
