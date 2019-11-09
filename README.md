# openapi-generatr-spring

a simple [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot].
 

# Getting Started

See the [documentation][generatr-doc].


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

[generatr-doc]: https://hauner.github.io/openapi-generatr-spring/
[generatr-int-resources]: https://github.com/hauner/openapi-generatr-spring/tree/master/src/testInt/resources
[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
