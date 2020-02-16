---
layout: default
title: Additional Parameters
parent: The processor
nav_order: 11
---

# Additional Parameters

Sometimes it may be useful to add an additional parameter to an endpoint that is an implementation detail.
It should not be part of the public api. Think of the `HttpServletRequest` or a custom Spring
 `HandlerMethodArgumentResolver`.
 
Such an additional parameter can be described in the mappings as an endpoint parameter. Assuming there
is an endpoint `/foo` defined in the OpenAPI interfaces it is possible to add extra parameters by using
an `add <paramter name>` `as <java type>` entry.

```yaml
    map:
      paths:
        /foo:

          parameters:
            - add: request
              as: javax.servlet.http.HttpServletRequest
```

will add the *additional* parameter to the generated interface method. 

```java
    @GetMapping(path = "/foo")
    ResponseEntity<?> getFoo(@RequestParam(name = "bar") String bar, HttpServletRequest request);
```

