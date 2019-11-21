---
layout: default
title: Interfaces
parent: The generatr
nav_order: 5
---

# Interfaces

The generatr groups endpoints based on their _first_ tag. Using the `/ping` example again its first
(and only) tag is **ping**: 

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

The interface name used for this api will be `PingApi`. `Ping` because `ping` is the tags name and
`Api` as a fixed string added to `Ping`. 

In case no tags are given, all endpoints will be added to an `Api` interface.

The package name gets created from the configurable `packageName` parameter of the generatr and a
sub package named `api`. 

If the `packageName` is configured as `com.github.hauner.openapi` the final package name for the 
interface is `com.github.hauner.openapi.api` and the full class & package name is 
`com.github.hauner.openapi.api.PingApi`.
