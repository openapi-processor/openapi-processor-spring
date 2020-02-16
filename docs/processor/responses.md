---
layout: default
title: Responses
parent: The processor
nav_order: 20
---

# Responses

All generated endpoints have a [`ResponseEntity<>`][spring-responseentity] result. This allows an endpoint
implementation full control of the response at the cost of having to provide a `ResponseEntity` even if
it could just return its pojo result.

Here are a few examples:

    public ResponseEntity<String> getFoo() {
        return ResponseEntity.ok("foo");
    }

    public ResponseEntity<Long> getBar() {
        return ResponseEntity.ok(5L);
    }

    public ResponseEntity<ResourceObject> getResourceObject() {
        return ResponseEntity.ok(resourceObject);
    }


Depending on the number of defined response content types the parameter of the `ResponseEntity<>` will
be either the java type or the *unknown type*.


| # responses   | ResponseEntity<>                     |
|:-------------:|:------------------------------------:| 
| one           | `ResponseEntity<java type>`          | 
| multiple      | `ResponseEntity<?>`                  | 






[spring-responseentity]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
