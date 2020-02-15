---
layout: default
title: Endpoints
parent: The generatr
nav_order: 6
---

# Endpoints

A simple path of the OpenAPI description will usually produce a single endpoint method in the target
interface as described in the introduction.

OpenAPI allows us to define more complex apis that behave differently based on the request header. 
For example the following api definition can return its response in different format. As json or as
plain text:

```yaml
openapi: 3.0.2
info:
  title: test multiple response content types
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '200':
          description: json or plain text result
          content:
            application/json:
                schema:
                  $ref: '#/components/schemas/Foo'
            text/plain:
                schema:
                  type: string
        default:
          description: error
          content:
            application/xml:
                schema:
                  $ref: '#/components/schemas/Error'

components:

  schemas:
    Foo:
      type: object
      properties:
        bar:
          type: string

    Error:
      type: object
      properties:
        error:
          type: string
```

A client request uses the request `Accept` header to tell the api which result content types it can
handle. 

Using a single endpoint method it has to decide which response to create. This leads to some boring
`if/else` code. To avoid this the generatr creates one endpoint method for each possible response.

For the example above it creates the following interface:

```java
package generated.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface Api {

    @GetMapping(
            path = "/foo",
            produces = {"application/json", "application/xml"})
    ResponseEntity<?> getFooApplicationJson();

    @GetMapping(
            path = "/foo",
            produces = {"text/plain", "application/xml"})
    ResponseEntity<?> getFooTextPlain();

}
```

The apis normal response (status '200') can return the result as json or as plain text which leads
to two methods for the same endpoint but with a different `produces` list in the mapping annotation.

One method which is called when json is requested and one when plain text is requested. Spring will
take care of selecting the correct endpoint.

In the (contrived) example our api does also define another content type for all other result status
codes: xml.

Both endpoints need to handle the success case (json or text) and the error (xml) case. So both
mappings contain the xml content type. With the different responses the `ResponseEntity` return type
is now the *unknown* type. Successful response and error properties are different which prevents an
explicit type as the result type.     
