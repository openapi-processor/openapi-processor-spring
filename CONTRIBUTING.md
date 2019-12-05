# contributing to openapi-generatr

## feature & bug reports

In case some feature is missing or the generated code is not 100% what you would expect please create
an issue with test case. Providing a test case will help significantly :-) 

A test case is a single folder with an openapi.yaml file and the expected Java files the generatr
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

The `mapping.yaml` contains the type mapping information and is an optional file.

See the [existing integration tests][generatr-int-resources] for a couple of examples.

## working on the code

### jdk

the minimum jdk is currently JDK 8

### ide setup

openapi-generatr can be imported into IntelliJ IDEA by opening the `build.gradle` file.
 
### running the tests

To run the tests use `./gradlew check`. 

`check` runs the unit tests and the integration tests  

### documentation

The documentation is in `docs`. See the `README` in `docs` for setup and viewing it locally.

