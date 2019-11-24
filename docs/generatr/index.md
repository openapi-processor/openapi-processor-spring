---
layout: default
title: The generatr
nav_order: 2
has_children: true
---

# The generatr

The openapi-generatr-spring is an **interface only** open api generatr. That means it will only
generate java interfaces for the endpoints. It is the projects task to implement them.

Let's take a look at a very simple example. The following open api yaml describes a single
endpoint. A call to the `/ping` endpoint will simply respond with a plain text string result. 

```yaml
    openapi: 3.0.2
    info:
      title: sample api for openapi-generatr-spring
      version: 1.0.0
    
    paths:
      /ping:
        get:
          tags:
            - ping
          summary: returns a single "pong" string.
          description: very simple sample endpoint
          responses:
            '200':
              description: pong
              content:
                text/plain:
                  schema:
                    type: string
```

Running the generatr on that open api yaml will create the following java interface:

```java
    package com.github.hauner.openapi.api;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.GetMapping;
    
    public interface PingApi {
    
        @GetMapping(path = "/ping", produces = {"text/plain"})
        ResponseEntity<String> getPing();
    
    }
```

It is now up to the project to implement the interface. For example like this:

```java
    package com.github.hauner.openapi;
    
    import com.github.hauner.openapi.api.PingApi;
    import org.springframework.http.ResponseEntity;
    import org.springframework.stereotype.Controller;
    
    @Controller
    public class PingController implements PingApi {
    
        @Override
        public ResponseEntity<String> getPing () {
            return ResponseEntity.ok ("pong");
        }
    
    }
```

That's it. 

The other sections provide some more detail about what is generated from which input.
