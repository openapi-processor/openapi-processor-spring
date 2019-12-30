---
layout: default
title: OpenAPI - describing a file upload endpoint
parent: HowTo's
nav_order: 15
---

Describing and endpoint for uploading a singe file:

```yaml
    /attachments:
      summary: upload an attachment
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
        responses:
          204:
            description: succesfully created attachment
            content:
              application/json:
                schema:
                  type: integer
                  format: int64
```

Describing an endpoint for uploading multiple files:

```yaml
    /attachments:
      summary: upload multiple attachments
      post:
        requestBody:
          content:
            multipart/form-data:
              schema:
                type: object
                properties:
                  files:
                    type: array
                    items:
                      type: string
                      format: binary
        responses:
          204:
            description: succesfully created attachment
            content:
              application/json:
                schema:
                  type: array
                  items:
                    type: string
                    format: int64
```
