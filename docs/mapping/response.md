---
layout: default
title: Response Mappings
parent: Type Mapping
nav_order: 16
---

# (global) Response Mappings 

Global response mapping will replace the result type of an endpoint in the api description based on
 its **content type** to the given java type.
 
It is defined like below and it should be added to the `map/responses` section in the mapping.yaml
which is a list of global response mappings. 
  
A single global response mapping can have the following properties:
 
 ```yaml
 - content: ..
   to: ..
   generics:
     - ..
     - ..
 ```

**content** and **to** are required.
 
**content** is the name of the content type of an endpoint response that should be replaced
  by **to**. 
  
**to** is the fully qualified class name of the java type that should be used for all endpoint
 content types with name **content**. 

**generics** defines the list of types that should be used as generic type parameters to the 
java type given by **to**.
 
<div markdown="1">
**Important:**
 
Since the generatr will simply match the content type string take care that all responses of this
content type should really use the same type!

This is probably only useful for vendor content types. Globally mapping the content type for example
 of `application/json` does not look like a good idea.
</div>{: .note .important .mb-6}


## Example

Given the following (global) response mapping
 
```yaml
     map:
     
       # list of global parameter mappings, mapped by parameter name
       responses:
         - content: application/vnd.something
           to: com.github.hauner.openapi.Something 
```

and an openapi.yaml with multiple endpoints returning their result as content type `application/vnd.something`

 ```yaml
    openapi: 3.0.2
    info:
      title: global response content type mapping example
      version: 1.0.0
    
    paths:
      /do-something:
        get:
          responses:
            '200':
              description: none ```
              content:
                application/vnd.something:
                  schema:
                    type: string
    
      /do-something-else:
        get:
          responses:
            '200':
              description: none ```
              content:
                application/vnd.something:
                  schema:
                    type: string
 ```


the generatr will use `com.github.hauner.openapi.Something` as java type for **all** responses with
the content type `application/vnd.something`.
