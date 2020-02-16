---
layout: default
title: Using Gradle
nav_order: 9
---

# Using Gradle
{: .no_toc }

Note: this page is for the gradle plugin since version '1.0.0.M6'. The new plugin provides proper
up-to-date checking (it does not re-run the processor when the api yaml is unchanged) and uses a
simpler configuration.
{: .note .info .mb-6}


The [openapi-processor-gradle][oap-gradle] is currently the only tool to run an **openapi-processor**. 

To use it in a gradle project the gradle file of the project requires a few additional instructions.
The following sections describe how to activate & configure **openapi-processor-spring** in a
`build.gradle` file. 
{: .mb-6 }

## table of contents
{: .no_toc .text-delta }

1. replaced by toc
{:toc}


# adding the plugin

The [openapi-processor-gradle][oap-gradle] plugin is activated (like any other gradle plugin) in
 the `plugins` configuration: 

        plugins {
            ....
            // add openapi-processor-gradle plugin
            id 'com.github.hauner.openapi-processor' version '<version>'
        }
        
        
# configuring openapi-processor-spring

The plugin will add an `openapiProcessor` configuration block that is used to configure the processors.
Configuration for a specific processor is placed inside it using the processor name as configuration
block name.   

        openapiProcessor {

            spring {
                processor 'com.github.hauner.openapi:openapi-processor-spring:<version>'
                apiPath "$projectDir/src/api/openapi.yaml"
                targetDir "$projectDir/build/openapi"
                mapping "$projectDir/openapi-mapping.yaml"
                showWarnings true
            }        

        }

- `processor`: (**required**) the processor dependency. This works in the same way as adding a dependency
 to a configuration in the gradle `dependencies` block. It is given here to avoid un-wanted side effects
  on the build dependencies of the project.
        
- `apiPath`: (**required**) the path to the `openapi.yaml` file and the main input for the processor. If
set in the top level block it will be used for all configured processors.

- `targetDir`: (**required**) the output folder for generating interfaces & models. This is the parent
 of the `packageName` folder tree. It is recommended to set this to a subfolder of gradle's standard `build`
directory so it is cleared by the `clean` task and does not pollute the sources directory.
 
  See [running the processor][docs-running] how to include the `targetDir` in compilation & packing.  

- `mapping`: (**required**, since 1.0.0.M6) provides the processor mapping options. This is a path
 to yaml file. See [Configuration][docs-configuration] for a description of the mapping yaml. This replaces
 the `typeMappings` option. 

- `showWarnings`: (**optional**) `true` to show warnings from the open api parser or `false` (default) to
 show no warnings.


   **Deprecated** the following options are deprecated starting with '1.0.0.M6'
   See [Configuration][docs-configuration].  
   {: .note .deprecated .mb-6}

- `typeMappings`: (**optional**) defines the type mapping if required. This is either a path to yaml
 file or a yaml string (i.e. the content of the yaml file). See [java type mapping][docs-mapping] for a
 description of the mapping yaml. 
 
  starting with '1.0.0.M6' this is replaced by the `mapping` option.

- `packageName`: (**required**) the root package name of the generated interfaces & models. The package folder
 tree will be created inside `targetDir`. 
 
  Interfaces and models will be generated into the `api` and `model` subpackages of `packageName`.

  - so the final package name of the generated interfaces will be `"${packageName}.api"`  
  - and the final package name of the generated models will be `"${packageName}.model"`  
  {: .mb-5 }

  starting with '1.0.0.M6' it is recommended to provide this in the mapping yaml. See
  [Configuration][docs-configuration].
  {: .mb-5 }

# running processor-spring

The plugin will add a gradle task `processSpring` to run the processor. 

To automatically generate & compile the processor output two additional configurations are required.

- the `sourceSets` are extended to include the processor output (assuming a java project):

        sourceSets {
            main {
                java {
                    // add generated files
                    srcDir 'build/openapi'
                }
            }
        }
 
 
 - and the `compileJava` task gets a dependency on `processSpring` so it runs before compilation (again,
  assuming a java project):  

        // generate api before compiling
        compileJava.dependsOn ('processSpring')

Adding automatic compilation in this way will also automatically include the generated files into the
`jar` build artifact. 


[oap-gradle]: https://github.com/hauner/openapi-processor-gradle
[docs-mapping]: /openapi-processor-spring/mapping/
[docs-configuration]: /openapi-processor-spring/processor/configuration.html
[docs-running]: #running-processor-spring
