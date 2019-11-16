---
layout: default
title: Basic Mappings
parent: Type Mapping
nav_order: 1
---

# Basic Mappings

The OpenAPI specification defines a couple of basic [data types][openapi-spec-types]{:target="_blank"}.
The basic data types are built-in in the generatr. That means the generatr will map the basic types
automatically to a corresponding java type. There is no explicit type mapping required.

## OpenAPI to Java type mapping

The following table shows the automatic mapping of types from OpenAPI to Java.

`type`    | `format`    | generatr Java type  
------    | --------    | ------------------  
`integer` |             | `java.lang.Integer`          
`integer` | `int32`     | `java.lang.Integer`          
`integer` | `int64`     | `java.lang.Long`             
`number`  |             | `java.lang.Float`
`number`  | `float`     | `java.lang.Float`
`number`  | `double`    | `java.lang.Double`
`string`  |             | `java.lang.String`
`string`  | `byte`      | not (yet) implemented
`string`  | `binary`    | not (yet) implemented
`boolean` |             | `java.lang.Boolean`
`string`  | `date`      | `java.time.LocalDate`  
`string`  | `date-time` | `java.time.OffsetDataTime` (since 1.0.0.A2)
`string`  | `password`  | ignored

[openapi-spec-types]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#dataTypes
