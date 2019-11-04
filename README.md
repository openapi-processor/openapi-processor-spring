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

## limitations
- property names in the openapi description must be java compatible (i.e. no `@JsonProperty` yet on model classes)
- limited parameter support
   - query parameters (i.e. `in: query`) 
       - does handle basic data types and `object`s
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

#### `x-java-type`

> no longer supported
> 
> an `x-java-type` extension looks like a simple solution to help the generatr create the expected
> code. But has a significant drawback. It has to be part of the api description. As an api provider
> I don't care which technology is used to access my api. So I don't want to add any technology specific
> details in the api description.

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
implementation full control of the response at the cost of having to provide a `ResponseEntity` even if
it could just return its pojo result.

## Endpoint Parameters

todo

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

> no longer supported, will be re-implemented using type mappings

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
[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
