---
layout: default
title: Mapping Structure
parent: Type Mapping
nav_order: 5
---

# mapping.yaml structure

A type mapping yaml has multiple sections to define the different kinds of type mappings. All sections
 are optional.

```yaml
    map:
    
      # list of global mappings
      types:
        - from: ..
          to: ..

      # list of global parameter mappings, mapped by parameter name
      parameters:
        - name: ..
          from: ..
          to: ..

      # list of global content mappings, mapped by content type
      responses:    
        - content: ..
          from: ..
          to: ..

      # path mappings, only valid for the given path
      paths:

        # the path
        /foo...:

          # list of path specific parameter mappings, mapped by parameter name
          parameters:
            - name: ..
              from: ..
              to: ..

          # list of path specific content mappings, mapped by content type
          responses:    
            - content: ..
              from: ..
              to: ..

```
