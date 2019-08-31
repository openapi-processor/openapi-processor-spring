# What's this?

an (experimental) simple opinionated OpenAPI interface only code generator for Spring Boot. 

# Status

(August 2019) work in progress, not yet usable except for simple apis.  



# Documentation

## OpenAPI to Java type mapping

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

The generatr does support an OpenAPI extension for array schemas. By adding the `x-java-type`
extension to the array schema it is possible to override the default:

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


The generatr needs to know the given type to generate proper java code so we can't simply add
a random collection type. The generatr does currently recognize the following types:

- java.util.Collection 


# Sample

See `openapi-generatr-spring-mvc-sample` for a complete working sample for a minimal openapi.yaml.
