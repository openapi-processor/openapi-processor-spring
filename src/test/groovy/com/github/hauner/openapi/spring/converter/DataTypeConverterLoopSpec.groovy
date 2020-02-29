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

import com.github.hauner.openapi.spring.model.datatypes.LazyDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse


class DataTypeConverterLoopSpec extends Specification {

    void "handles \$ref loops"() {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: test \$ref loop
  version: 1.0.0

paths:

  /self-reference:
    get:
      responses:
        '200':
          description: none
          content:
            application/json:
                schema:
                  \$ref: '#/components/schemas/Self'

components:
  schemas:

    Self:
      type: object
      properties:
        self:
          \$ref: '#/components/schemas/Self'
""")

        when:
        def api = new ApiConverter (new ApiOptions()).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rp = ep.getFirstResponse ('200')
        def rt = rp.responseType
        def sf = rt.objectProperties.self
        rt instanceof ObjectDataType
        sf instanceof LazyDataType
        sf.name == 'Self'
        sf.packageName == 'generatr.model'
        sf.imports == ['generatr.model.Self'] as Set
        sf.referencedImports == ['generatr.model.Self'] as Set
    }

}
