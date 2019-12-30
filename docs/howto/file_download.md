---
layout: default
title: OpenAPI - describing a file download endpoint
parent: HowTo's
nav_order: 10
---

Describing an endpoint to download file attachments.

```yaml
    /v1/attachments/{id}:
      get:
        summary: download an attachment
        parameters:
          - in: path
            name: id
            required: true
            schema:
              type: integer
              format: int64
        responses:
          200:
            description: attachment data
            headers:
              Content-Disposition:
                schema:
                  type: string
                description: the format is `attachment; filename="name.zip"`
            content:
              application/*:
                schema:
                  type: string
                  format: binary
              image/*:
                schema:
                  type: string
                  format: binary
```
