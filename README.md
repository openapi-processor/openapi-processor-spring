# What's this?

an (experimental) simple opinionated [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot]
for server side implementation. 

expectations:

- generates only Java interfaces and Java model classes (get/set pojos) for the defined endpoints and schemas to 
  allow (nearly) full control of the endpoint implementation.

- interfaces and models are implemented & compiled by your project (that is easily done with gradle). The generatr
  does not generate any other files.
  
- the generated code does not use swagger annotations. It does not make sense (to me) to generate the documentation
 from the code when I generated the code from the documentation (i.e. an openapi.yaml). 
 
- it generates simple code.

- it allows type mappings (with generic values) to map schemas defined in the openapi.yaml to existing java classes.
 This includes Spring types like `Page<>` & `Pageable`.
 
- it allows to add additional parameters to an endpoint. For example to pass the `HttpServletRequest` to the controller
method.

- it handles multiple responses by generating one controller method for each response content type.

- WebFlux support, may need its own generatr.   


# Status

(November 2019) this is work in progress.

current status & limitations:

## status

- generates interfaces & models for all endpoints
- supports query parameters (i.e. `in: query`) for basic and object types
- supports responses with basic and object types
- supports type mappings with generics (one level only) 

## known limitations
- property names in the openapi description must be java compatible (i.e. no `@JsonProperty` yet on model classes)
- limited parameter support
   - no path parameters (i.e. `in: path`)
   - no header parameters (i.e. `in: header`)
   - no cookie parameters (i.e. `in: cookie`)
- honors only the first response content description
- MVC only, no WebFlux
- there are probably more limitations ... ;-) 

# Documentation

[OpenAPI specification][openapi-spec].

## OpenAPI to Java type mapping

[OpenAPI data types][openapi-spec-types]

### simple data types

The following table shows the supported data type mappings.

`type`    | `format`    | generatr Java type  
------    | --------    | ------------------  
`integer` |             | `Integer`          
`integer` | `int32`     | `Integer`          
`integer` | `int64`     | `Long`             
`number`  |             | `Float`
`number`  | `float`     | `Float`
`number`  | `double`    | `Double`
`string`  |             | `String`
`string`  | `byte`      | not implemented
`string`  | `binary`    | not implemented
`boolean` |             | `Boolean`
`string`  | `date`      | `LocalDate`  
`string`  | `date-time` | not implemented (`Instant`, `ZonedDateTime`, `OffsetDataTime`, configurable ?)
`string`  | `password`  | ignored


### array

By default the generatr maps the OpenAPI `array` type to a plain Java array. Given the following api
description: 

    /array-string:
    get:
      responses:
        '200':
          content:
            application/json:
              schema:
                type: array
                items:
                  type: string
          description: none

it will create the endpoint like this:

    @GetMapping(path = "/array-string", produces = {"application/json"})
    ResponseEntity<String[]> getArrayString();

#### java type mapping

By default the OpenAPI `array` is mapped to a simple java array. It is possible to change that default 
mapping for example to `java.util.Collection` by adding a type mapping to the generator options.

The `SpringGeneratrOptions` object has a property `typeMapping` that takes either a file name to a yaml
file (with extension `.yaml`) or an inline yaml. 

Given the api: 

    /array-collection:
    get:
      responses:
        '200':
          content:
            application/vnd.collection:
              schema:
                type: array
                items:
                  type: string
          description: none

and the following yaml:

    maps:
      types:
        - from array
          to java.util.Collection

the generated code will change to:

    @GetMapping(path = "/array-collection", produces = {"application/json"})
    ResponseEntity<Collection<String>> getArrayCollection();

using the `array`s `items` type as the generic parameter.

The generatr needs to know the given collection type to generate proper java code so we can't simply
add a random collection type. The generatr does currently recognize the following types:

- `java.util.Collection`
- `java.util.List`
- `java.util.Set`

## Endpoint Response

All generated endpoints have a [`ResponseEntity<>`][spring-responseentity] result. This allows an endpoint
implementation full control of the response at the cost of having to provide a `ResponseEntity` even if
it could just return its pojo result.

## Endpoint Parameters

### Query Parameters

#### simple query parameters

The following query parameter description 

    paths:
      /endpoint:
        get:
          parameters:
            - name: foo
              description: simple query parameter with default value
              in: query
              required: false
              schema:
                type: string
                default: 'not set'
          responses:
            '204':
              description: empty

will generate the following interface method:

    @GetMapping(path = "/endpoint")
    ResponseEntity<void> getEndpoint(@RequestParam(name = "foo", required = false, defaultValue = "not set") String foo);

# gradle

To use openapi-generatr-spring in a gradle project the gradle file of the project requires a few additional instructions.

1. the generatr itself is a `buildscript` dependency:

        buildscript {
          dependencies {
            // adds generatr-spring
            classpath 'com.github.hauner.openapi:openapi-generatr-spring:<version>'
          }
        }

2. the [openapi-generatr-gradle][generatr-gradle] is activated in the `plugins` configuration: 

        plugins {
            ....
            // add generatr-gradle plugin
            id 'com.github.hauner.openapi-generatr' version '<version>'
        }
        
3.  the plugin will find the generatr on the build classpath and adds a `generatrSpring` configuration block that is
    used to configure the generatr.

        generatrSpring {
            // the path to the open api yaml file.
            apiPath = "$projectDir/src/api/openapi.yaml"
    
            // the destination folder for generating interfaces & models. This is the parent of the
            // {packageName} folder tree.
            targetDir = "$projectDir/build/openapi"
    
            // the root package of the generated interfaces/model. The package folder tree will be
            // created inside {targetDir}. Interfaces and models will be placed into the "api" and
            // "model" subpackages of packageName:
            // - interfaces => "${packageName}.api"
            // - models => "${packageName}.model"
            packageName = "com.github.hauner.openapi.sample"
    
            // show warnings from the open api parser.
            showWarnings = true
            
            // mapping if required (see java type mapping)
            typeMapping = "$projectDir/openapi-mapping.yaml"
        }

4. the plugin will also add a gradle task `generateSpring` to run the generatr.
 
5. to automatically generate & compile the generatr output the `sourceSets` are extended to include the generatr output
and the `compileJava` task gets a dependency on `generateSpring` so the generatr runs before compilation:  

        sourceSets {
            main {
                java {
                    // add generated files
                    srcDir 'build/openapi'
                }
            }
        }

        // generate api before compiling
        compileJava.dependsOn ('generateSpring')


# Sample

See [`openapi-generatr-spring-mvc-sample`][generatr-sample] for a complete working sample of a minimal
 openapi.yaml.

# Features & Bugs

In case some feature is missing or the generated code is not 100% what you would expect create an issue
with test case. Providing a test case will help significantly :-) 

A test case is a single folder with an openapi.yaml file and the expected Java files the generatr should create.
The structure looks like this:

    my-new-test-case/
                     openapi.yaml
                     mapping.yaml
                     generated/
                               api/
                                  AnEndpointInterfaceApi.java
                                  .. more api interfaces ..
                               model/
                                     AModelClass.java
                                     AnotherModelClass.java
                                     .. more model files ..

The `mapping.yaml` contains the type mapping information and is an optional file.

See the [existing integration tests][generatr-int-resources] for a couple of examples. 


[openapi]: https://www.openapis.org/
[openapi-spec]: https://github.com/OAI/OpenAPI-Specification
[openapi-spec-types]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#dataTypes
[openapi-spec-exts]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#specificationExtensions

[springboot]: https://spring.io/projects/spring-boot
[spring-requestparam]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestParam.html
[spring-responseentity]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html

[generatr-int-resources]: https://github.com/hauner/openapi-generatr-spring/tree/master/src/testInt/resources
[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
