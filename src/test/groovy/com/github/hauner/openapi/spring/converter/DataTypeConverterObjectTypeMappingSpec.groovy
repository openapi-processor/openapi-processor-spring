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

import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.model.Api
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class DataTypeConverterObjectTypeMappingSpec extends Specification {

    void "converts named schemas to java type via global type mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /page:
    get:
      parameters:
        - in: query
          name: pageable
          required: false
          schema:
            \$ref: '#/components/schemas/Pageable'
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                \$ref: '#/components/schemas/StringPage'

components:
  schemas:

    Pageable:
      description: minimal Pageable query parameters
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer

    Page:
      description: minimal Page response without content property
      type: object
      properties:
        number:
          type: integer
        size:
          type: integer

    StringContent:
      description: specific content List of the Page response
      type: object
      properties:
        content:
          type: array
          items:
            type: string

    StringPage:
      description: typed Page
      type: object
      allOf:
        - \$ref: '#/components/schemas/Page'
        - \$ref: '#/components/schemas/StringContent'
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    sourceTypeName: 'Pageable',
                    targetTypeName: 'org.springframework.data.domain.Pageable'),
                new TypeMapping (
                    sourceTypeName: 'StringPage',
                    targetTypeName: 'org.springframework.data.domain.Page',
                    genericTypeNames: ['java.lang.String'])
            ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        def response = ep.response
        parameter.dataType.packageName == 'org.springframework.data.domain'
        parameter.dataType.name == 'Pageable'
        response.responseType.packageName == 'org.springframework.data.domain'
        response.responseType.name == 'Page<String>'
    }

    void "throws when there are multiple global mappings for a named schema" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /page:
    get:
      parameters:
        - in: query
          name: pageable
          required: false
          schema:
            \$ref: '#/components/schemas/Pageable'
      responses:
        '204':
          description: none

components:
  schemas:

    Pageable:
      description: minimal Pageable query parameters
      type: object
      properties:
        page:
          type: integer
        size:
          type: integer
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    sourceTypeName: 'Pageable',
                    targetTypeName: 'org.springframework.data.domain.Pageable'),
                new TypeMapping (
                    sourceTypeName: 'Pageable',
                    targetTypeName: 'org.springframework.data.domain.Pageable')
            ])
        new ApiConverter (options).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings == options.typeMappings
    }

    void "converts named schemas to java type via endpoint type mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foobar:
    get:
      parameters:
        - in: query
          name: foo
          required: false
          schema:
            \$ref: '#/components/schemas/Foo'
      responses:
        '200':
          description: none
          content:
            application/json:
              schema:
                \$ref: '#/components/schemas/Bar'

components:
  schemas:

    Foo:
      description: minimal query parameter object
      type: object
      properties:
        foo:
          type: string

    Bar:
      description: minimal response object
      type: object
      properties:
        bar:
          type: string

""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new EndpointTypeMapping (path: '/foobar',
                    typeMappings: [
                        new TypeMapping (
                            sourceTypeName: 'Foo',
                            targetTypeName: 'someA.ObjectA'),
                        new TypeMapping (
                            sourceTypeName: 'Bar',
                            targetTypeName: 'someB.ObjectB')])
            ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        def response = ep.response
        parameter.dataType.packageName == 'someA'
        parameter.dataType.name == 'ObjectA'
        response.responseType.packageName == 'someB'
        response.responseType.name == 'ObjectB'
    }

}
