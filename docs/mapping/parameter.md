---
layout: default
title: Parameter Mappings
parent: Type Mapping
nav_order: 13
---

# (global) Parameter Mappings

Global parameter mapping will replace any usage of an OpenAPI type in the api description based on
 the parameters **name** to the given java type.
 
It is defined like below and it should be added to the `map/parameters` section in the mapping.yaml
which is a list of global parameter mappings. 
  
A single global parameter mapping can have the following properties:
 
 ```yaml
 - name: ..
   to: ..
   generics:
     - ..
     - ..
 ```
 
**name** and **to** are required.
 
**name** is the name of an endpoint parameter used in the OpenAPI description that should be replaced
  by **to**.
  
**to** is the fully qualified class name of the java type that should be used for all endpoint
 parameters of name **name**. 

**generics** defines the list of types that should be used as generic type parameters to the 
java type given by **to**.
 
<div markdown="1">
**Important:**
 
Since the generatr will simply match the parameters by their name take care that all parameters of
that name should really use the same type!
</div>{: .note .important .mb-6}

## Example

Given the following (global) parameter mapping
 
```yaml
     map:
     
       # list of global parameter mappings, mapped by parameter name
       parameters:
         - name: date
           to: java.time.ZonedDateTime
```

and an openapi.yaml with multiple endpoints having a parameter named "date"

```yaml
    openapi: 3.0.2
    info:
      title: global parameter type mapping example
      version: 1.0.0
    
    paths:
      /do-something:
        get:
          parameters:
            - in: query
              name: date
              schema:
                type: int32
          responses:
            '200':
              description: none
              content:
                application/json:
                  schema:
                    type: object
                    properties:
                      prop:
                        type: string

      /do-something-else:
        get:
          parameters:
            - in: query
              name: date
              schema:
                type: string
          responses:
            '200':
              description: none
              content:
                application/json:
                  schema:
                    type: object
                    properties:
                      prop:
                        type: string
```

the generatr will use `java.time.ZonedDateTime` as java type for **all** parameters named "date" in
**all** endpoints that have a "date" parameter. 

In the example both endpoints would use `java.time.ZonedDateTime` as java type for the "date" parameter.
