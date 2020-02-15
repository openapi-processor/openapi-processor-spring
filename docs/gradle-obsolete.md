---
layout: default
title: Using Gradle (obsolete)
nav_order: 10
---

# Using Gradle (obsolete)
{: .no_toc }

**Deprecated**: this page is out-of-date, describing the previous version of the gradle plugin. See the latest
documentation [here][docs-gradle].
{: .note .deprecated .mb-6}

The [openapi-generatr-gradle][generatr-gradle] is currently the only way to run a **openapi-generatr.** 

To use it in a gradle project the gradle file of the project requires a few additional instructions.
The following sections describe how to activate & configure **generatr-spring** in a `build.gradle` file. 
{: .mb-6 }

## table of contents
{: .no_toc .text-delta }

1. replaced by toc
{:toc}


# adding the plugin

The [openapi-generatr-gradle][generatr-gradle] plugin is activated (like any other gradle plugin) in the `plugins`
configuration: 

        plugins {
            ....
            // add generatr-gradle plugin
            id 'com.github.hauner.openapi-generatr' version '<version>'
        }
        
# adding generatr-spring

The plugin will automatically find the generatr in the `buildscript` classpath.
 
If there is no `buildscript` block in the `build.gradle` copy the whole block below to the beginning of the
`build.gradle` file.

If there is already a `buildscript` add the additional maven repository and generatr-spring as `classpath`
dependency.

        buildscript {
          repositories {
             maven {
               url  "https://dl.bintray.com/hauner/openapi-generatr"
            }
          }
          
          dependencies {
            // adds generatr-spring
            classpath 'com.github.hauner.openapi:openapi-generatr-spring:<version>'
          }
        }


# configuring generatr-spring

The plugin will add a `generatrSpring` configuration block that is used to configure the generatr.

        generatrSpring {
            apiPath = "$projectDir/src/api/openapi.yaml"
            typeMappings = "$projectDir/openapi-mapping.yaml"

            targetDir = "$projectDir/build/openapi"
            packageName = "com.github.hauner.openapi.sample"
    
            showWarnings = true
        }
        
        
- `apiPath`: (**required**) the path to the `openapi.yaml` file and the main input for the generatr.

- `typeMappings`: (**optional**) defines the type mapping if required. This is either a path to yaml
 file or a yaml string (i.e. the content of the yaml file). See [java type mapping][docs-mapping] for a
 description of the mapping yaml.

- `targetDir`: (**required**) the output folder for generating interfaces & models. This is the parent
 of the `packageName` folder tree. It is recommended to set this to a subfolder of gradle's standard `build`
directory so it is cleared by the `clean` task and does not pollute the sources directory.
 
  See [running the generatr][docs-running] how to include the `targetDir` in compilation & packing.  

- `packageName`: (**required**) the root package name of the generated interfaces & models. The package folder
 tree will be created inside `targetDir`. 
 
  Interfaces and models will be generated into the `api` and `model` subpackages of `packageName`.

  - so the final package name of the generated interfaces will be `"${packageName}.api"`  
  - and the final package name of the generated models will be `"${packageName}.model"`  
{: .mb-5 }

- `showWarnings`: (**optional**) `true` to show warnings from the open api parser or `false` (default) to
 show no warnings.


# running generatr-spring

The plugin will add a gradle task `generateSpring` to run the generatr. 

To automatically generate & compile the generatr output two additional configurations are required.

- the `sourceSets` are extended to include the generatr output (assuming a java project):

        sourceSets {
            main {
                java {
                    // add generated files
                    srcDir 'build/openapi'
                }
            }
        }
 
 
 - and the `compileJava` task gets a dependency on `generateSpring` so it runs before compilation (again,
  assuming a java project):  

        // generate api before compiling
        compileJava.dependsOn ('generateSpring')

Adding automatic compilation in this way will also automatically include the generated files into the
`jar` build artifact. 


[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[docs-mapping]: /openapi-generatr-spring/mapping/
[docs-gradle]: /openapi-generatr-spring/gradle.html
[docs-running]: #running-generatr-spring
