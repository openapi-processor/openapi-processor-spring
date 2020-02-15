[![][badge-license]][generatr-license]
[![][badge-ci]][workflow-ci]

# openapi-generatr-spring

a simple [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot].
 

# Getting Started

See the [documentation][generatr-doc].


# Sample

See [`openapi-generatr-spring-mvc-sample`][generatr-sample] for a complete working sample of a minimal
 openapi.yaml.

# Features & Bugs

In case some feature is missing or the generated code is not 100% what you would expect create an issue
with test case. Providing a test case will help significantly :-) 

A test case is a single folder with an openapi.yaml file and the expected Java files the generatr should create.
The structure looks like this:

    my-new-test-case/
                     openapi.yaml
                     mapping.yaml
                     generated/
                               api/
                                  AnEndpointInterface.java
                                  .. more api interfaces ..
                               model/
                                     AModelClass.java
                                     AnotherModelClass.java
                                     .. more model files ..

The `mapping.yaml` contains the type mapping information.

See the [existing integration tests][generatr-int-resources] for a couple of examples. 

[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[badge-ci]: https://github.com/hauner/openapi-generatr-spring/workflows/ci/badge.svg

[workflow-ci]: https://github.com/hauner/openapi-generatr-spring/actions?query=workflow%3Aci

[openapi]: https://www.openapis.org/
[openapi-spec]: https://github.com/OAI/OpenAPI-Specification
[openapi-spec-types]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#dataTypes
[openapi-spec-exts]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#specificationExtensions

[springboot]: https://spring.io/projects/spring-boot
[spring-requestparam]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestParam.html
[spring-responseentity]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html

[generatr-license]: https://github.com/hauner/openapi-generatr-spring/blob/master/LICENSE
[generatr-doc]: https://hauner.github.io/openapi-generatr-spring/
[generatr-int-resources]: https://github.com/hauner/openapi-generatr-spring/tree/master/src/testInt/resources
[generatr-gradle]: https://github.com/hauner/openapi-generatr-gradle
[generatr-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
