/*
 * Copyright 2019 https://github.com/hauner/openapi-spring-generator
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

import com.github.hauner.openapi.spring.support.ModelAsserts
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterSpec extends Specification implements ModelAsserts {

    void "groups endpoints into interfaces by first operation tag" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    get:
      tags:
        - ping
      responses:
        'default':
          description: none
  /b:
    get:
      tags:
        - pong
      responses:
        'default':
          description: none
  /c:
    get:
      tags:
        - ping
        - pong
      responses:
        'default':
          description: none
""")

        when:
        api = new ApiConverter ().convert (openApi)

        then:
        assertInterfaces ('ping', 'pong')
        assertPingEndpoints ('/a', '/c')
        assertPongEndpoints ('/b')
    }


    @Unroll
    void "groups endpoints with method #method into interfaces" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    ${method}:
      tags:
        - ${method}
      responses:
        'default':
          description: none
""")

        when:
        api = new ApiConverter ().convert (openApi)

        then:
        assertInterfaces (method)
        assertEndpoints (method,'/a')

        where:
        method << ['get', 'put', 'post', 'delete', 'options', 'head', 'patch', 'trace']
    }

}
