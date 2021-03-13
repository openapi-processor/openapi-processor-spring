# contributing to openapi-processor

## feature & bug reports

In case some feature is missing, or the generated code is not 100% what you would expect please create an issue with test case. Providing a test case will help significantly :-) Such a test case is used to run an end-to-end test of the openapi-processor. 

A test case is a folder with the name of the integration test.

It contains an `openapi.yaml` and  a `mapping.yaml` file in an `inputs` sub folder. An `inputs.yaml` sibling file of the `inputs` folder lists the files in the folder.

To provide the expected output, i.e. the java code, the test case contains another sub folder called `generated`. A `generated.yaml` sibling file lists the files the folder.

`generated` is the root package-name of the generated code, and it contains the target `api` and `model` packages of the processor.

### test case layout

the inputs:

```
resources/tests/my-test
+--- inputs.yaml
\--- inputs
  +--- mapping.yaml
  \--- openapi.yaml
```

the expected files:

```
 resources/tests/my-test
 +--- generated.yaml
 \--- generated
      +--- api
      |    \--- EndpointApi.java
      \--- model
           \--- Foo.java
```

the `inputs.yaml` and `generated.yaml` use the same simple format:

```
 items:
    - inputs/openapi.yaml
    - inputs/mapping.yaml
```

or

```
 items:
    - generated/api/EndpointApi.java
    - generated/model/Foo.java
```

The `mapping.yaml` contains the type mapping information and is an optional file, but it is recommended to provide one. If there is no `mapping.yaml` the test runner will use a minimal version: 

```aml
openapi-processor-spring: v2

options:
  package-name: generated
```

See the existing integration tests of [oap-core][oap-core-int-resources] & [oap-spring][oap-spring-int-resources] for examples.

## working on the code

### jdk

the minimum jdk is currently JDK 8

### ide setup

openapi-processor can be imported into IntelliJ IDEA by opening the `build.gradle` file.
 
### running the tests

To run the tests use `./gradlew check`. 

`check` runs the unit tests, and the integration tests  

### documentation

The documentation is in `docs`. See the `README` in `docs` for setup and viewing it locally.

[oap-core-int-resources]: https://github.com/hauner/openapi-processor-core/tree/master/src/testInt/resources
[oap-spring-int-resources]: https://github.com/hauner/openapi-processor-spring/tree/master/src/testInt/resources
