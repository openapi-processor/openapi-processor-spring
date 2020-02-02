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
import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.model.Api
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class DataTypeConverterArrayTypeMappingSpec extends Specification {

    @Unroll
    void "maps array schema to #responseTypeName via global type mapping" () {
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
            application/vnd.any:
              schema:
                type: array
                items:
                  type: string
          description: none              
""")
        when:
        def options = new ApiOptions(packageName: 'pkg', typeMappings: [
            new TypeMapping (sourceTypeName: 'array', targetTypeName: targetTypeName)
        ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == responseTypeName

        where:
        targetTypeName         | responseTypeName
        'java.util.Collection' | 'Collection<String>'
        'java.util.List'       | 'List<String>'
        'java.util.Set'        | 'Set<String>'
    }

    void "throws when there are multiple global mappings for the array type" () {
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
            type: array
            items: 
              type: string
      responses:
        '204':
          description: none
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: [
                new TypeMapping (
                    sourceTypeName: 'array',
                    targetTypeName: 'java.util.Collection'),
                new TypeMapping (
                    sourceTypeName: 'array',
                    targetTypeName: 'java.util.Collection')
            ])
        new ApiConverter (options).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)
        e.typeMappings == options.typeMappings
    }


    @Unroll
    void "throws when there are multiple mappings on the same level: #type" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      parameters:
        - in: query
          name: param
          required: false
          schema:
            type: array
            items: 
              type: string
      responses:
        '204':
          description: none
""")

        when:
        def options = new ApiOptions(
            packageName: 'pkg',
            typeMappings: mappings)
        new ApiConverter (options).convert (openApi)

        then:
        def e = thrown (AmbiguousTypeMappingException)

        where:
        type << [
            'global type mappings',
            'global io mappings',
            'endpoint mappings'
        ]

        mappings << [
            [
                new TypeMapping (
                    sourceTypeName: 'array',
                    targetTypeName: 'java.util.Collection'),
                new TypeMapping (
                    sourceTypeName: 'array',
                    targetTypeName: 'java.util.Collection')
            ],
            [
                new ParameterTypeMapping (
                    parameterName: 'param',
                    mapping: new TypeMapping (
                        sourceTypeName: 'array',
                        targetTypeName: 'java.util.Collection')
                ),
                new ParameterTypeMapping (
                    parameterName: 'param',
                    mapping: new TypeMapping (
                        sourceTypeName: 'array',
                        targetTypeName: 'java.util.Collection')
                )
            ],
            [
                new EndpointTypeMapping (path: '/foo',
                    typeMappings: [
                        new ParameterTypeMapping (
                            parameterName: 'param',
                            mapping: new TypeMapping (
                                sourceTypeName: 'array',
                                targetTypeName: 'java.util.Collection')
                        ),
                        new ParameterTypeMapping (
                            parameterName: 'param',
                            mapping: new TypeMapping (
                                sourceTypeName: 'array',
                                targetTypeName: 'java.util.Collection')
                        )
                    ])
            ]
        ]
    }

    void "converts array response schema to #responseTypeName via endpoint type mapping" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /foo:
    get:
      responses:
        '200':
          content:
            application/vnd.any:
              schema:
                type: array
                items:
                  type: string
          description: none
""")

        when:
        def options = new ApiOptions(packageName: 'pkg', typeMappings: [
            new EndpointTypeMapping (path: '/foo',
                typeMappings: [
                    new TypeMapping (
                        sourceTypeName: 'array',
                        targetTypeName: targetTypeName)
                    ])
        ])
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == responseTypeName
        rsp.responseType.packageName == 'java.util'

        where:
        targetTypeName         | responseTypeName
        'java.util.Collection' | 'Collection<String>'
        'java.util.List'       | 'List<String>'
        'java.util.Set'        | 'Set<String>'
    }

    @Unroll
    void "converts array parameter schema to java type via #type" () {
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
            type: array
            items:
              type: string
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
        def p = ep.parameters.first ()
        p.dataType.name == 'Collection<String>'
        p.dataType.packageName == 'java.util'

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
                                sourceTypeName: 'array',
                                targetTypeName: 'java.util.Collection')
                        )
                    ])
            ], [
                new ParameterTypeMapping (
                    parameterName: 'foobar',
                    mapping: new TypeMapping (
                        sourceTypeName: 'array',
                        targetTypeName: 'java.util.Collection')
                )
            ]
        ]
    }

    @Unroll
    void "converts array response schema to Collection<> via #type" () {
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
            application/vnd.any:
              schema:
                type: array
                items:
                  type: string
          description: none              
""")

        when:
        def options = new ApiOptions(packageName: 'pkg', typeMappings: mappings)
        Api api = new ApiConverter (options).convert (openApi)

        then:
        def itf = api.interfaces.first ()
        def ep = itf.endpoints.first ()
        def rsp = ep.getFirstResponse ('200')
        rsp.responseType.name == 'Collection<String>'
        rsp.responseType.imports == ['java.util.Collection', 'java.lang.String'] as Set

        where:
        type << [
            'endpoint response mapping',
            'global response mapping',
            'endpoint response mapping over endpoint type mapping',
            'endpoint type mapping'
        ]

        mappings << [
            [
                new EndpointTypeMapping (path: '/array-string',
                    typeMappings: [
                        new ResponseTypeMapping (
                            contentType: 'application/vnd.any',
                            mapping: new TypeMapping (
                                sourceTypeName: 'array',
                                targetTypeName: 'java.util.Collection')
                        )
                    ]
                )
            ], [
                new ResponseTypeMapping (
                    contentType: 'application/vnd.any',
                    mapping: new TypeMapping (
                        sourceTypeName: 'array',
                        targetTypeName: 'java.util.Collection')
                )
            ], [
                new EndpointTypeMapping (path: '/array-string',
                    typeMappings: [
                        new ResponseTypeMapping (
                            contentType: 'application/vnd.any',
                            mapping: new TypeMapping (
                                sourceTypeName: 'array',
                                targetTypeName: 'java.util.Collection')
                        ),
                        new TypeMapping (
                            sourceTypeName: 'array',
                            targetTypeName: 'java.util.Collection')
                    ]
                )
            ], [
                new EndpointTypeMapping (path: '/array-string',
                    typeMappings: [
                        new TypeMapping (
                            sourceTypeName: 'array',
                            targetTypeName: 'java.util.Collection')
                    ]
                )
            ]
        ]
    }

}
