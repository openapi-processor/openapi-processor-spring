---
layout: default
title: The processor
nav_order: 2
has_children: true
---

# The processor

The openapi-processor-spring is an **interface only & model** open api generator. That means it will
only generate java interfaces for the endpoints and the required model POJO classes. It is the
projects task to implement the endpoint interfaces.

Let's take a look at a very simple example. The following open api yaml describes a single
endpoint. A call to the `/ping` endpoint will simply respond with a plain text string result. 

```yaml
    openapi: 3.0.2
    info:
      title: openapi-processor-spring sample
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

Running the processor on that open api yaml will create the following java interface:

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
