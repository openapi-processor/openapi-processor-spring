---
layout: default
title: Spring Pageable & Page
parent: HowTo's
nav_order: 5
---

# generatr: using Spring Pageable & Page 

The given (lengthy) openapi yaml example describes a pageable api in two variations. The `/page`
endpoint uses named objects and the second endpoint `/page-inline` uses inline objects to describe
the paging parameters and the page response. We fill focus on the first variation.

Describing the `Pageable` parameters as an `object` tells us that they belong together.
 
Describing the `Page` result is a bit more complicated. OpenAPI does not have generic types which we
would like to have to define what type is in the content list of the page.
 
The best OpenAPI way is to define two objects. The first one describes the common properties of the
`Page` response and the second one the content list of the page. In this example the `StringContent`
with an array of `string`.

Using OpenAPIs `allOf` construct we join both objects to describe a complete response that corresponds
to a Spring `Page` object.

Splitting the `Page` object helps if we have multiple endpoints with paging because we do not have to
repeat the common properties for every endpoint.   

```yaml
openapi: 3.0.2
info:
  title: Spring Page/Pageable API
  version: 1.0.0

paths:
  /page:
    get:
      parameters:
        - in: query
          name: pageable
          required: false
          schema:
            $ref: '#/components/schemas/Pageable'
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/StringPage'

  /page-inline:
    get:
      parameters:
        - in: query
          name: pageable
          required: false
          schema:
            type: object
            properties:
              page:
                type: integer
              size:
                type: integer
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                type: object
                allOf:
                  - $ref: '#/components/schemas/Page'
                  - $ref: '#/components/schemas/StringContent'

components:
  schemas:

    Pageable:
      description: minimal Pageable query parameters
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer

    Page:
      description: minimal Page response without content property
      type: object
      properties:
        number:
          type: integer
        size:
          type: integer

    StringContent:
      description: specific content List of the Page response
      type: object
      properties:
        content:
          type: array
          items:
            type: string

    StringPage:
      description: typed Page
      type: object
      allOf:
        - $ref: '#/components/schemas/Page'
        - $ref: '#/components/schemas/StringContent'
```

The generatr does create a proper interface with both endpoints if we provide a type mappings for the
`Pageable` and `Page` types. 

Here is the java code we expect:

```java
package generated.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

public interface Api {

    @GetMapping(
            path = "/page",
            produces = {"application/json"})
    ResponseEntity<Page<String>> getPage(Pageable pageable);

    @GetMapping(
            path = "/page-inline",
            produces = {"application/json"})
    ResponseEntity<Page<String>> getPageInline(Pageable pageable);

}
```

and here is the required mapping:

```yaml
map:

  types:
    - from: Pageable
      to: org.springframework.data.domain.Pageable

    - from: StringPage
      to: org.springframework.data.domain.Page<java.lang.String>

  paths:

    /page-inline:

      parameters:
        - name: pageable
          to: org.springframework.data.domain.Pageable

      responses:
        - content: application/json
          to: org.springframework.data.domain.Page<java.lang.String>
```
Usually you would use the first variation using named objects so they can be re-used on other endpoints.

In that case you will only need the global type mapping of `Pageable` and the `StringPage`. Note that
the `StringPage` uses a generic parameter.

Worth mentioning is that the generatr will not generate model classes for the openapi types `Pageable`,
`Page`, `StringContent` or `StringPage`.
