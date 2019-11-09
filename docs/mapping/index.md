---
layout: default
title: Type Mapping
nav_order: 3
has_children: true
---

# Type Mapping

Using type mapping we can tell the generatr to map types (schemas) from an openapi.yaml description to
a specific existing java type instead of generating a model class from the source openapi type. 

For example to map the openapi `array` type to different java collections or to map paging parameters
 and results to Spring types like `Page<>` & `Pageable`.
   
Type mapping is very flexible. It is possible to define the mapping globally, globally for a specific
 response or parameter or even limited to a specific endpoint. 

Type mapping also supports generic parameters to the target type. One level. That means you can provide
generic types for target type but not for nested types.
