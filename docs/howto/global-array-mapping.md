---
layout: default
title: global 'array' mapping
parent: HowTo's
nav_order: 1
---

# generatr: map openapi array (globally) to a java collection type 

By default the OpenAPI `array` is mapped to a simple java array. It is possible to change that default 
mapping for example to `java.util.Collection` by adding a type mapping to the [`mapping.yaml`][docs-mapping].

Given the following openapi.yaml fragment: 

```yaml
    paths:
     
      /array:
        get:
          responses:
            '200':
              description: 
              content:
                application/json:
                  schema:
                    type: array
                    items:
                      type: string
```

the generatr will create the following endpoint interface:

```java
    @GetMapping(path = "/array", produces = {"application/json"});
    ResponseEntity<String[]> getArray();
```

To globally change the mapping of `array` to another collection type we just need to add a simple entry
to the [`mapping.yaml`][docs-mapping]. Adding the following *global* type mapping.

```yaml
    map:
      types:
    
        # map array to java.util.Collection
        - from: array
          to: java.util.Collection
```

will change the generated endpoint to:

```java
    @GetMapping(path = "/array", produces = {"application/json"});
    ResponseEntity<Collection<String>> getArray();
```

using the `array`s `items` property as the generic parameter of `Collection`.

The generatr needs to know the given collection type to generate proper java code so we can't simply
add a random collection type. The generatr does currently recognize the following types:

- `java.util.Collection`
- `java.util.List`
- `java.util.Set`



[docs-mapping]: /openapi-generatr-spring/mapping/
