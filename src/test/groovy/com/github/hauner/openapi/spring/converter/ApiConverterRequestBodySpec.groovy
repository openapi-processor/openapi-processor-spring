/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.core.converter.ApiConverter
import com.github.hauner.openapi.core.converter.ApiOptions
import com.github.hauner.openapi.core.converter.MultipartResponseBodyException
import com.github.hauner.openapi.core.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.core.converter.mapping.TypeMapping
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterRequestBodySpec extends Specification {

    void "converts request body parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test request body parameter
  version: 1.0.0

paths:
  /endpoint:

    get:
      tags:
        - endpoint
      requestBody:
        content:
          application/json:
            schema:
              type: object
              properties:
                foo:
                  type: string
      responses:
        '204':
          description: empty
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def body = ep.requestBodies.first ()
        body.contentType == 'application/json'
        body.requestBodyType.type == 'EndpointRequestBody'
        !body.required
        body.annotation == '@RequestBody'
        body.annotationWithPackage == 'org.springframework.web.bind.annotation.RequestBody'
    }

    void "converts request body multipart/form-data object schema properties to request parameters" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart-form-data
  version: 1.0.0

paths:
  /multipart/single-file:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                other:
                  type: string
      responses:
        '204':
          description: empty
"""
        )

        def options = new ApiOptions(packageName: 'pkg', typeMappings: [
            new EndpointTypeMapping('/multipart/single-file', [
                new TypeMapping (
                    'string',
                    'binary',
                    'org.springframework.web.multipart.MultipartFile')
            ])
        ])

        when:
        def api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def file = ep.parameters[0]
        def other = ep.parameters[1]

        file.name == 'file'
        file.required
        file.dataType.name == 'MultipartFile'
        file.dataType.imports == ['org.springframework.web.multipart.MultipartFile'] as Set
//        file.withAnnotation ()
//        file.annotation == '@RequestParam'
//        file.annotationWithPackage == 'org.springframework.web.bind.annotation.RequestParam'

        other.name == 'other'
        other.required
        other.dataType.name == 'String'
//        file.withAnnotation ()
//        other.annotation == '@RequestParam'
//        other.annotationWithPackage == 'org.springframework.web.bind.annotation.RequestParam'
    }

    void "throws when request body multipart/form-data schema is not an object schema" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart-form-data
  version: 1.0.0

paths:
  /multipart/broken:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: string
      responses:
        '204':
          description: empty
"""
        )

        when:
        new ApiConverter ().convert (openApi)

        then:
        def e = thrown(MultipartResponseBodyException)
        e.path == '/multipart/broken'
    }

    void "does not register the object data type of a request body multipart/form-data schema to avoid model creation" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: params-request-body-multipart-form-data
  version: 1.0.0

paths:
  /multipart/single-file:
    post:
      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                other:
                  type: string
      responses:
        '204':
          description: empty
"""
        )

        when:
        def cv = new ApiConverter ().convert (openApi)

        then:
        cv.models.objectDataTypes.empty
    }

}
