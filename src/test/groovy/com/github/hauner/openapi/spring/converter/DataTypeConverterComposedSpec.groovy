/*
 * Copyright 2020 the original authors
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

import com.github.hauner.openapi.spring.model.datatypes.ComposedObjectDataType
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class DataTypeConverterComposedSpec extends Specification {

    void "converts allOf composed schema object"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: composed schema
  version: 1.0.0

paths:

  /endpoint:
    get:
      responses:
        '200':
          description: allOf
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Foo'

components:
  schemas:
  
    Foo:
      allOf:
        - \$ref: '#/components/schemas/FooA'

    FooA:      
      type: object
      properties:
        fooA:
          type: string
  
""")

        when:
        def api = new ApiConverter ().convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'Foo'

        def cs = rsp.responseType as ComposedObjectDataType
        cs.items.size () == 1
        cs.items.first ().name == 'FooA'
    }

}
