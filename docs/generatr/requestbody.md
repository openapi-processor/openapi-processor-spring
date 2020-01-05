---
layout: default
title: Request Body
parent: The generatr
nav_order: 15
---

# Request Body

This OpenAPI describes an endpoint with `requestBody`:

```yaml
    openapi: 3.0.2
    info:
      title: request body
      version: 1.0.0
    
    paths:
      /book:
        post:
          requestBody:
            content:
              application/json:
                schema:
                  $ref: '#/components/schemas/Book'
            required: true
          responses:
            '201':
              description: created book
              content:
                application/json:
                  schema:
                    $ref: '#/components/schemas/Book'
    
    components:
      schemas:
        Book:
          type: object
          properties:
            isbn:
              type: string
            title:
              type: string
```

that the generatr will convert to the interface:

```java
    package generated.api;

    import generated.model.Book;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    
    public interface Api {
    
        @PostMapping(path = "/book", consumes = {"application/json"}, produces = {"application/json"})
        ResponseEntity<Book> postBook(@RequestBody Book body);
    
    }
```

and a `Book` pojo.

## multipart/form-data <span class="label label-green">since 1.0.0.M5</span>

For file uploads, where the `content` of the `requestBody` is `multipart/form-data`, the resulting
code looks a bit different and requires a specific type mapping.

The file upload in OpenAPI is described like this:

```yaml
    /file-upload:
      summary: upload a file
      post:
        requestBody:
          content:
            multipart/form-data:
              schema:
                type: object
                properties:
                  file:
                    type: string
                    format: binary
```

(See also [OpenAPI - describing a file upload endpoint][howto-file-upload])

The `schema` must be of type `object` defining a property for each part in the multipart body.

Instead of generating a `@RequestBody` parameter for the `object` schema the generatr creates
a parameter for each property of the object annotated with `@RequestParam`:

```java
    package generated.api;
    
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.multipart.MultipartFile;
    
    public interface Api {
    
        @PostMapping(path = "/file-upload")
        ResponseEntity<Void> postFileUpload(@RequestParam(name = "file") MultipartFile file);
    
    }
```

Note that the `file` property (type `string` format `binary`) is mapped to Springs `MultipartFile`
type. The generatr does not have a default mapping for the `binary` format so to get the code
above we have to configure it in the type mapping yaml:

```yaml
    map:
      paths:
        /file-upload:
          types:
            - from: string:binary
              to: org.springframework.web.multipart.MultipartFile
```

To upload multiple files we can define the body `object` with an `array` property: 

```yaml
    type: object
    properties:
      files:
        type: array
        items:
          type: string
          format: binary
```

to get the following code:

```java
    @PostMapping(path = "/file-upload")
    ResponseEntity<Void> postFileUpload(@RequestParam(name = "files") MultipartFile[] files);
```


[howto-file-upload]: /openapi-generatr-spring/howto/file_upload.html

