/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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

import com.github.hauner.openapi.spring.model.Api
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterSchemaSpec extends Specification {

    void "creates model for component schema object with simple properties" () {
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

        and:
        def model = api.models.get (0)
        assert model.name == 'Book'
        assert model.properties.size () == 2
        def isbn = model.properties.get(0)
        assert isbn.type == 'string'
        assert isbn.name == 'isbn'
        def title = model.properties.get(1)
        assert title.type == 'string'
        assert title.name == 'title'
    }

    void "creates map<string, string> model for inline object type"() {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /inlineObject:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
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
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.type == 'map'
    }
    
    void "creates model for a basic data type with optional format #type/#format" () {
        def yaml = """\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /format:
    get:
      responses:
        '200':
          description: none
          content:
            application/vnd.integer:
              schema:
                type: $type
"""

        if (format != null) {
            yaml += """\
                format: $format
"""
        }
        def openApi = parse (yaml)

        when:
        Api api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.type == type
        ep.response.responseType.format == format

        where:
        type      | format
        'string'  | null
        'integer' | null
        'integer' | 'int32'
        'integer' | 'int64'
        'number'  | null
        'number'  | 'float'
        'number'  | 'double'
        'boolean' | null
    }

}
