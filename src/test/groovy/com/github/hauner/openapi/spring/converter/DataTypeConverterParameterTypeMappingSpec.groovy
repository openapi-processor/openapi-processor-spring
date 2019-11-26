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
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.model.Api
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class DataTypeConverterParameterTypeMappingSpec extends Specification {

    @Unroll
    void "converts object parameter schema to java type via #type" () {
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
          name: foobar
          required: false
          schema:
            type: object
            properties:
              foo:
                type: integer
              bar:
                type: integer
      responses:
        '204':
          description: empty
""")

        when:
        def options = new ApiOptions(packageName: 'pkg', typeMappings: mappings)
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.parameters.first ().dataType.name == 'TargetClass'

        where:
        type << [
            'endpoint parameter mapping',
            'global parameter mapping'
        ]

        mappings << [
            [
                new EndpointTypeMapping (path: '/foobar',
                    typeMappings: [
                        new ParameterTypeMapping (
                            parameterName: 'foobar',
                            mapping: new TypeMapping (
                                sourceTypeName: 'object',
                                targetTypeName: 'pkg.TargetClass')
                        )
                    ])
            ], [
                new ParameterTypeMapping (
                    parameterName: 'foobar',
                    mapping: new TypeMapping (
                        sourceTypeName: 'object',
                        targetTypeName: 'pkg.TargetClass')
                )
            ]
        ]
    }

}
