---
layout: default
title: Query Parameters
parent: The generatr
nav_order: 10
---

# Query Parameters

The following query parameter description: 

```yaml
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
```

will generate the following interface method:

```java
    @GetMapping(path = "/endpoint")
    ResponseEntity<Void> getEndpoint(@RequestParam(name = "foo", required = false, defaultValue = "not set") String foo);
```
