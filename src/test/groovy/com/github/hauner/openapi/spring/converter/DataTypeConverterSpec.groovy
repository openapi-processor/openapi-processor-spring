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

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import io.swagger.v3.oas.models.media.Schema
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class DataTypeConverterSpec extends Specification {
    def converter = new DataTypeConverter()


    void "creates none data type" () {
        when:
        def type = converter.none ()

        then:
        type
    }

    @Unroll
    void "converts schema(#type, #format) to #javaType" () {
        Schema schema = new Schema(type: type, format: format)

        when:
        def datatype = converter.convert (schema, null, [])

        then:
        datatype.name == javaType

        where:
        type      | format   | javaType
        'string'  | null     | 'String'
        'integer' | null     | 'Integer'
        'integer' | 'int32'  | 'Integer'
        'integer' | 'int64'  | 'Long'
        'number'  | null     | 'Float'
        'number'  | 'float'  | 'Float'
        'number'  | 'double' | 'Double'
        'boolean' | null     | 'Boolean'
    }

    void "throws when hitting an unknown data type" () {
        Schema schema = new Schema(type: type, format: format)

        when:
        converter.convert (schema, null, [])

        then:
        def e = thrown(UnknownDataTypeException)
        e.type == type
        e.format == format

        where:
        type | format
        'x'  | null
        'y'  | 'none'
    }

    void "converts simple array schema to Array[]" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /array-string:
    get:
      responses:
        '200':
          content:
            application/vnd.array:
              schema:
                type: array
                items:
                  type: string
          description: none              
""")
        when:
        def options = new ApiOptions(packageName: 'pkg')
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.name == 'String[]'
    }

    void "converts simple array schema to Collection<> set via x-java-type" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /array-string:
    get:
      responses:
        '200':
          content:
            application/vnd.collection:
              schema:
                type: array
                x-java-type: java.util.Collection
                items:
                  type: string
          description: none              
""")
        when:
        def options = new ApiOptions(packageName: 'pkg')
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        ep.response.responseType.name == 'Collection<String>'
    }


    void "creates model for inline response object with name {path}Response{response code}"() {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /inline:
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
        def options = new ApiOptions(packageName: 'pkg')
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def props = ep.response.responseType.properties
        ep.response.responseType.name == 'InlineResponse200'
        ep.response.responseType.packageName == "${options.packageName}.model"
        props.size () == 2
        props.get ('isbn').name == 'String'
        props.get ('title').name == 'String'

        and:
        api.models.size () == 1
        api.models.first () is ep.response.responseType
    }

    void "creates model for component schema object" () {
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
        def options = new ApiOptions(packageName: 'pkg')
        Api api = new ApiConverter (options).convert (openApi)

        then:
        api.models.size () == 1

        and:
        def dataType = api.models.first ()
        assert dataType.name == 'Book'
        assert dataType.packageName == "${options.packageName}.model"
        assert dataType.properties.size () == 2
        def isbn = dataType.properties.get('isbn')
        assert isbn.name == 'String'
        def title = dataType.properties.get('title')
        assert title.name == 'String'
    }
}
