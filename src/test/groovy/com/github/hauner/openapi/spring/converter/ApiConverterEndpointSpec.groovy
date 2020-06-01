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

import com.github.hauner.openapi.core.model.Api
import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.support.ModelAsserts
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterEndpointSpec extends Specification implements ModelAsserts {

    void "creates model for an endpoint with a component schema object with simple properties" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: component schema object
  version: 1.0.0

paths:
  /book:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Book'

components:
  schemas:
    Book:
      type: object
      properties:
        isbn:
          type: string
        title:
          type: string
""")
        when:
        Api api = new ApiConverter ().convert (openApi)

        then:
        api.models.size () == 1
        api.interfaces.size () == 1

        and:
        def itf = api.interfaces.get (0)
        def ep = itf.endpoints.get(0)
        def rsp = ep.getFirstResponse ('200')
        rsp.contentType == 'application/json'
        rsp.responseType.name == 'Book'
    }

    void "creates model for an endpoint without parameters and a single response content type" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: Ping API
  version: 1.0.0

paths:
  /ping:
    get:
      tags:
        - ping
      responses:
        '200':
          description: string result
          content:
            text/plain:
              schema:
                type: string
""")

        when:
        Api api = new ApiConverter ().convert (openApi)

        then:
        api.interfaces.size () == 1
        api.interfaces.get(0).endpoints.size () == 1

        and:
        def itf = api.interfaces.get (0)
        def ep = itf.endpoints.get(0)
        def rsp = ep.getFirstResponse ('200')
        ep.path == '/ping'
        ep.method == HttpMethod.GET
        rsp.contentType == 'text/plain'
        rsp.responseType.name == 'String'
    }

    void "creates model for an endpoint without parameters and without response content type" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: Ping API
  version: 1.0.0

paths:
  /ping:
    get:
      tags:
        - ping
      responses:
        '204':
          description: no content
""")

        when:
        Api api = new ApiConverter ().convert (openApi)

        then:
        api.interfaces.size () == 1
        api.interfaces.get(0).endpoints.size () == 1

        and:
        def itf = api.interfaces.get (0)
        def ep = itf.endpoints.get(0)
        def rsp = ep.getFirstResponse ('204')
        ep.path == '/ping'
        ep.method == HttpMethod.GET
        !rsp.contentType
        rsp.responseType instanceof NoneDataType
    }


    void "uses default interface name when no interface-name-tag is given" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /endpoint:
    get:
      responses:
        '204':
          description: no content
""")

        when:
        api = new ApiConverter ().convert (openApi)

        then:
        api.interfaces.get (0).name == ApiConverter.INTERFACE_DEFAULT_NAME
    }

    void "keeps order of endpoints"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /endpoint:
    get:
      responses:
        '204':
          description: no content

    post:
      responses:
        '204':
          description: no content
""")

        when:
        api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.get (0)
        itf.endpoints.get(0).method == HttpMethod.GET
        itf.endpoints.get(1).method == HttpMethod.POST
    }

}
