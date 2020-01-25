---
layout: default
title: Mapping Structure
parent: Type Mapping
nav_order: 5
---

# type mapping structure

The type mapping is part of the mapping yaml (see [Configuration][docs-configuration]) and configured under
the `map` key. The `map` key contains multiple sections to define the different kinds of type mappings.
 All sections are optional.

```yaml
    map:
    
      # list of global mappings
      types:
        - from: ..
          to: ..

      # list of global parameter mappings, mapped by parameter name
      parameters:
        - name: ..
          to: ..

      # list of global content mappings, mapped by content type
      responses:    
        - content: ..
          to: ..

      # path mappings, only valid for the given path
      paths:

        # the path
        /foo...:

          # list of path specific mappings
          types:
            - from: ..
              to: ..

          # list of path specific parameter mappings, mapped by parameter name
          parameters:
            - name: ..
              to: ..

          # list of path specific content mappings, mapped by content type
          responses:    
            - content: ..
              to: ..

```

[docs-configuration]: /openapi-generatr-spring/generatr/configuration.html
