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

package com.github.hauner.openapi.core.converter

import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse


class DataTypeConverterEnumSpec extends Specification {

    void "converts enum query parameter"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test enum parameters
  version: 1.0.0

paths:

  /endpoint:
    get:
      tags:
        - enum
      parameters:
        - name: foo
          description: enum parameter
          in: query
          schema:
            type: string
            enum:
              - foo
              - bar
              - foo-bar
      responses:
        '204':
          description: empty
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def param = ep.parameters.first ()
        param.name == 'foo'
        param.dataType.name == 'Foo'
        param.dataType.values == ['foo', 'bar', 'foo-bar']
    }

    void "remembers inline & named enum schema data types"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test enum parameters
  version: 1.0.0

paths:

  /endpoint:
    get:
      tags:
        - enum
      parameters:
        - name: foo
          description: enum parameter
          in: query
          schema:
            type: string
            enum:
              - foo
              - foo-2
              - foo-foo
        - name: bar
          description: enum parameter
          in: query
          schema:
            \$ref: '#/components/schemas/Bar'
      responses:
        '204':
          description: empty

components:
  schemas:

    Bar:
      type: string
      enum:
        - bar
        - bar-2
        - bar-bar
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        api.models.dataTypes.size () == 2
        api.models.dataTypes[0].name == 'Foo'
        api.models.dataTypes[1].name == 'Bar'
    }

}
