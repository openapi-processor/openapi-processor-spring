---
layout: default
title: Responses
parent: The generatr
nav_order: 10
---

## Responses

All generated endpoints have a [`ResponseEntity<>`][spring-responseentity] result. This allows an endpoint
implementation full control of the response at the cost of having to provide a `ResponseEntity` even if
it could just return its pojo result.

[spring-responseentity]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html
