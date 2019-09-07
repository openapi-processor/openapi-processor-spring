# What's this?

an (experimental) simple opinionated [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot]. 

# Status

(September 2019) work in progress, not yet usable except for simple apis.  


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
`string`  | `date`      | not implemented (`LocalDate`)  
`string`  | `date-time` | not implemented (`Instant`, `ZonedDateTime` ?)
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

#### `x-java-type`

The generatr does support an [OpenAPI extension][openapi-spec-exts] for array schemas. By adding the
 `x-java-type` extension to the array schema it is possible to override the default:

    /array-collection:
    get:
      responses:
        '200':
          content:
            application/vnd.collection:
              schema:
                type: array
                x-java-type: java.util.Collection
                items:
                  type: string
          description: none

The

    x-java-type: java.util.Collection
    
line will change the endpoint to:

    @GetMapping(path = "/array-collection", produces = {"application/json"})
    ResponseEntity<Collection<String>> getArrayCollection();


The generatr needs to know the given type to generate proper java code so we can't simply add a random
 collection type. The generatr does currently recognize the following types:

- `java.util.Collection` 


# Sample

See [`openapi-generatr-spring-mvc-sample`][generatr-sample] for a complete working sample of a minimal
 openapi.yaml.



[openapi]: https://www.openapis.org/
[openapi-spec]: https://github.com/OAI/OpenAPI-Specification
[openapi-spec-types]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#dataTypes
[openapi-spec-exts]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#specificationExtensions

[springboot]: https://spring.io/projects/spring-boot

[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
