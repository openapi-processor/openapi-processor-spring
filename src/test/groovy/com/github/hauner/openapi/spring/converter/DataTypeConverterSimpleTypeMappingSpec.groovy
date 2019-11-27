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

import com.github.hauner.openapi.spring.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.model.Api
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class DataTypeConverterSimpleTypeMappingSpec extends Specification {

    void "converts basic types with format to java type via global type mapping" () {
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
          name: date
          required: false
          schema:
            type: string
            format: date-time
      responses:
        '204':
          description: none
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    sourceTypeName: 'string',
                    sourceTypeFormat: 'date-time',
                    targetTypeName: 'java.time.ZonedDateTime')
            ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def parameter = ep.parameters.first ()
        parameter.dataType.packageName == 'java.time'
        parameter.dataType.name == 'ZonedDateTime'
    }

    void "throws when there are multiple global mappings for a simple type" () {
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
          name: date
          required: false
          schema:
            type: string
            format: date-time
      responses:
        '204':
          description: none
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    sourceTypeName: 'string',
                    sourceTypeFormat: 'date-time',
                    targetTypeName: 'java.time.ZonedDateTime'),
                new TypeMapping (
                    sourceTypeName: 'string',
                    sourceTypeFormat: 'date-time',
                    targetTypeName: 'java.time.ZonedDateTime')
            ])
        new ApiConverter (options).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings == options.typeMappings
    }

}
