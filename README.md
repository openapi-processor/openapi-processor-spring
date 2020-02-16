[![][badge-license]][oap-license]
[![][badge-ci]][workflow-ci]

![openapi-processor-spring logo](images/openapi-processor-spring@1280x200.png)

# openapi-processor-spring

a simple [OpenAPI][openapi] interface only code generator for [Spring Boot][springboot].
 

# Getting Started

See the [documentation][oap-doc].


# Sample

See [`openapi-processor-spring-mvc-sample`][oap-sample] for a complete working sample of a minimal
 openapi.yaml.

# Features & Bugs

In case some feature is missing or the generated code is not 100% what you would expect create an
 issue with test case. Providing a test case will help significantly :-) 

A test case is a single folder with an openapi.yaml file and the expected Java files the processor
 should create. The structure looks like this:

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

See the [existing integration tests][oap-int-resources] for a couple of examples. 

[badge-license]: https://img.shields.io/badge/License-Apache%202.0-blue.svg?labelColor=313A42
[badge-ci]: https://github.com/hauner/openapi-generatr-spring/workflows/ci/badge.svg

[workflow-ci]: https://github.com/hauner/openapi-processor-spring/actions?query=workflow%3Aci

[openapi]: https://www.openapis.org/
[openapi-spec]: https://github.com/OAI/OpenAPI-Specification
[openapi-spec-types]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#dataTypes
[openapi-spec-exts]: https://github.com/OAI/OpenAPI-Specification/blob/master/versions/3.0.2.md#specificationExtensions

[springboot]: https://spring.io/projects/spring-boot
[spring-requestparam]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/bind/annotation/RequestParam.html
[spring-responseentity]: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/http/ResponseEntity.html

[oap-license]: https://github.com/hauner/openapi-generatr-spring/blob/master/LICENSE
[oap-doc]: https://hauner.github.io/openapi-processor-spring/
[oap-int-resources]: https://github.com/hauner/openapi-processor-spring/tree/master/src/testInt/resources
[oap-gradle]: https://github.com/hauner/openapi-processor-gradle
[oap-sample]: https://github.com/hauner/openapi-generatr-spring-mvc-sample
