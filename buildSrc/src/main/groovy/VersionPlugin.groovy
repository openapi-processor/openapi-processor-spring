import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * provides a "generateVersion" task to a create a simple Version.java class:
 *
 * <pre>{@code
 * package io.openapiprocessor.spring;
 *
 * public class Version {
 *     public static final String version = "${project.version}";
 * }
 * }</pre>
 *
 *
 * The io/openapiprocessor/spring/Version.java file is generated to:
 *
 * $(project.buildDir}/main/java
 *
 * Add it as a source directory to include it in compilation.
 */
class VersionPlugin implements Plugin<Project> {

    void apply(Project project) {
        project.afterEvaluate (new Action<Project> () {

            @Override
            void execute (Project prj) {
                prj.tasks.register ('generateVersion', VersionTask , new Action<VersionTask>() {

                    @Override
                    void execute (VersionTask task) {
                        task.targetDir = prj.buildDir
                        task.version = prj.version
                    }

                })
            }

        })
    }

}
