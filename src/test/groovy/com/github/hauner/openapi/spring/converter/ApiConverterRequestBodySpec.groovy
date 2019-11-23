/*
 * Copyright 2019 the original authors
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

}
