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

## Endpoint Response

All generated endpoints have a [`ResponseEntity<>`][spring-responseentity] result. This allows an endpoint
implementation full control of the response. 


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

#### object query parameter

In case multiple query parameters should be mapped into a single model object like in this description:

    paths:
      /endpoint:
        get:
          parameters:
            - name: props
              description: object via multiple query parameters 
              in: query
              required: false
              schema:
                type: object
                properties:
                  prop1:
                    type: string
                  prop2:
                    type: string
              style: form
              explode: true
          responses:
            '204':
              description: empty

the generatr will by default create this interface method:

    @GetMapping(path = "/endpoint")
    ResponseEntity<void> getEndpointObject(@RequestParam Props props); 

Default values for the object properties will be ignored even if defined. Springs [`@RequestParam`][spring-requestparam]
annotation does not support default values for multiple properties. The generatr also ignores `style` & `exploded`
which are mainly interesting for client side code generation. 

If the controller method should be one of the following variations

    @GetMapping(path = "/endpoint")
    ResponseEntity<void> getEndpointObject(@RequestParam(name = "foo") Map foo); 

> used to to convert the query parameter `foo` to a map.
    
    @GetMapping(path = "/endpoint")
    ResponseEntity<void> getEndpointObject(@RequestParam Map<String,String> allParams); 

> populates the map with all query parameter names and values.

    @GetMapping(path = "/endpoint")
    ResponseEntity<void> getEndpointObject(@RequestParam MultiValueMap<String,String> allParams); 

> same as the previous but can handle multiple query parameters of the same name

the generatr needs some help to generate the proper code. Adding the `x-type-java` extension property to the schema
tells the generatr which Java type it should use.  

    parameters:
      - name: props
        description: object via multiple query parameters 
        in: query
        schema:
          type: object
          x-java-type: java.util.Map
          properties:
            prop1:
              type: string
            prop2:
              type: string

The possible values of `x-type-java` for query parameters are          

- `java.util.Map`
  
  this should be used to convert a single query parameter to a map. It is the first variation above.
    
- `java.util.Map<>`

  this should be used to add all query parameter to a single map. It is the second variation above.

- `org.springframework.util.MultiValueMap`

  this should be used to add all query parameter (which may have multiple parameters with the same name) to a single
  multi value map. It is the third variation above.


# Sample

See [`openapi-generatr-spring-mvc-sample`][generatr-sample] for a complete working sample of a minimal
 openapi.yaml.



[openapi]: https://www.openapis.org/
[openapi-spec]: https://github.com/OAI/OpenAPI-Specification
[openapi-spec-types]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#dataTypes
[openapi-spec-exts]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#specificationExtensions

[springboot]: https://spring.io/projects/spring-boot

[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
