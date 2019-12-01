---
layout: default
title: Identifiers
parent: The generatr
nav_order: 15
---

# Identifiers

## general

The generatr will map identifiers used in the OpenAPI description (i.e. `yaml` file) to valid Java
identifiers.

The Java identifiers will use camel case, starting with a upper case letter if it is a type name and
a lower case letter if it is a variable name.  

Camel case will be produced by detecting word breaks on special characters and using an upper case
first letter on the next word. The special characters are:

- characters that are not allowed in java identifiers (for example a `-` (minus)). This is checked
 by using [`Character.isJavaIdentifierStart()`][java-char-start] and
    [`Character.isJavaIdentifierPart()`][java-char-part]  

- `_` (underscore). The underscore is allowed in java identifiers but usually not used

## model

For properties of model classes, the properties will be annotated with `@JsonProperty` to provide
the mapping from the OpenAPI identifier to the Java identifier.

```java
    class Example {

        @JsonProperty("foo-bar")
        private String fooBar;
        
        // ...
    }
```

[java-char-start]: https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/Character.html#isJavaIdentifierStart(char)
[java-char-part]: https://docs.oracle.com/en/java/javase/13/docs/api/java.base/java/lang/Character.html#isJavaIdentifierPart(char)

[jackson-json-property]: https://fasterxml.github.io/jackson-annotations/javadoc/2.8/com/fasterxml/jackson/annotation/JsonProperty.html
