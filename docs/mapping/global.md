---
layout: default
title: Global Mappings
parent: Type Mapping
nav_order: 10
---

# Global Mappings
{: .no_toc }


Global type mapping will replace **any** usage of an OpenAPI type in the api description to the given
java type.
 
It is defined like below and it should be added to the `map/types` section in the mapping.yaml which
is a list of global type mappings. 
 
A single global mapping can have the following properties:

```yaml
- from: ..
  to: ..
  generics:
    - ..
    - ..
```

**from** and **to** are required, **generics** is optional.

**from** is the type name used in the OpenAPI description and names the type that should be replaced
by **to**. **to** is the fully qualified class name of the java type that should be used instead of
**from**. **generics** defines the list of types that should be used as generic type parameters to the 
java type given by **to**.


## simple mapping

In the simplest form a global type mapping of an OpenAPI `object` schema like this one:  

```yaml
Book:
  type: object
  properties:
    isbn:
      type: string
    title:
      type: string
```

can be mapped to an existing `Book` java type/class by the following mapping:

```yaml
- from: Book
  to: com.github.hauner.openapi.generatr.Book
```

It is also possible to use a predefined OpenAPI type as the `from` type of a type mapping: 

```yaml
- from: array
  to: java.util.List
```

This tells the generatr to us a `java.util.List` instead of the OpenAPI type `array`.
 
The **generics** parameter is not required in this special case. The generatr knows `java.util.List`
and will automatically use the `items` property of the `array` as the generic type.  

<div markdown="1">
**Important:**
- OpenAPIs `object` type has no special handling if given as the `from` type. The generatr assumes
that it is just a schema name and it will only match if there is schema with the name "object".   
- global type mappings do not work on OpenAPI inline schemas. Inline schemas do not have a name so
there is no way for the generatr to recognize it.
</div>{: .note .important .mb-6}

## mapping basic (primitive) types with format

The basic types in OpenAPI can have a `format` modifier. For example the `string` type has two
modifiers `date` and `date-time` to provide more detail of the type. In this case the kind of date
that is represented as a `string`.

It is possible to create a global mapping that only matches a specific `format` by adding the format
to the **from** property value separated by a ':' like this: 

```yaml
- from: string:date-time
  to: java.time.ZonedDateTime
```

This maps the `string` type with `date-time` format from the default `java.time.OffsetDateTime` to
`java.time.ZonedDateTime`. `string` without format or `string` with other formats are not affected
by this mapping.

## mapping with generic types

Type mapping allows to use a target type that has generic parameters. The generic types are defined
by the **generics** property of the mapping. **generics** is a list and can contain multiple types.

For example if a `StringPage` schema is defined in the OpenAPI that corresponds to
`org.springframework.data.domain.Page<java.lang.String>`, it can be mapped to the Spring type by:
 
```yaml
- from: StringPage
  to: org.springframework.data.domain.Page
  generics:
    - java.lang.String
```

The generatr will replace any use of `StringPage` with the **to** type and add the generic types
 (in the given order) to the **to** type. 
 
In case of the example above the generatr will create `Page<String>` instead of `StringPage` with an
additional `import` for the generic type (.. ignoring imports on `java.lang`).

<div markdown="1">
**Important:**

The generatr does support only one level of generics. It is not possible to provide generic parameters
to generic parameters.
</div>{: .note .important .mb-6}

To get a more compact description it is possible to write a shorter mapping by inlining the generic
types: 

```yaml
- from: StringPage
  to: org.springframework.data.domain.Page<java.lang.String>
```

This will generate the same code as the longer mapping version above.
