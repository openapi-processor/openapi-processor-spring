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

package com.github.hauner.openapi.spring.processor

import com.github.hauner.openapi.core.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.core.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.core.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.core.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.core.converter.mapping.ResultTypeMapping
import com.github.hauner.openapi.core.converter.mapping.TypeMapping
import com.github.hauner.openapi.core.processor.MappingConverter
import com.github.hauner.openapi.core.processor.MappingReader
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class MappingConverterV2Spec extends Specification {

    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()

    @Unroll
    void "reads global type mapping: (#input.source)" () {
        String yaml = """\
openapi-processor-spring: v2.0

map:
  types:
    - type: ${input.source}
"""

        if (input.generics) {
            yaml += """\
      generics:
"""
        }

        input.generics?.each {
            yaml += """\
        - ${it} 
"""
        }

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as TypeMapping
        type.sourceTypeName == input.expected.sourceTypeName
        type.sourceTypeFormat == input.expected.sourceTypeFormat
        type.targetTypeName == input.expected.targetTypeName
        type.genericTypeNames == input.expected.genericTypeNames

        where:
        input << [
            [
                // normal
                source: 'array => java.util.Collection',
                expected: new TypeMapping (
                    'array',
                    null,
                    'java.util.Collection',
                    [])
            ], [
                // extra whitespaces
                source: '  array   =>    java.util.Collection   ',
                expected: new TypeMapping (
                    'array',
                    null,
                    'java.util.Collection',
                    [])
            ], [
                // with format
                source: 'string:date-time => java.time.ZonedDateTime',
                expected: new TypeMapping (
                    'string',
                    'date-time',
                    'java.time.ZonedDateTime',
                    [])
            ], [
                // extra whitespaces with format
                source  : '"  string  :  date-time   =>    java.time.ZonedDateTime   "',
                expected: new TypeMapping (
                    'string',
                    'date-time',
                    'java.time.ZonedDateTime',
                    [])
            ], [
                // with inline generics
                source: 'Foo => mapping.Bar<java.lang.String, java.lang.Boolean>',
                expected: new TypeMapping (
                    'Foo',
                    null,
                    'mapping.Bar',
                    ['java.lang.String', 'java.lang.Boolean'])
            ], [
                // with extracted generics
                source: 'Foo => mapping.Bar',
                generics: ['java.lang.String', 'java.lang.Boolean'],
                expected: new TypeMapping (
                    'Foo',
                    null,
                    'mapping.Bar',
                    ['java.lang.String', 'java.lang.Boolean'])
            ],  [
                // inline generics with extra whitespaces
                source: 'Foo => mapping.Bar  <   java.lang.String  ,   java.lang.Boolean   >   ',
                expected: new TypeMapping (
                    'Foo',
                    null,
                    'mapping.Bar',
                    ['java.lang.String', 'java.lang.Boolean'])
            ], [
                // extracted generics with extra whitespaces
                source: 'Foo => mapping.Bar',
                generics: ['   java.lang.String   ', '   java.lang.Boolean   '],
                expected: new TypeMapping (
                    'Foo',
                    null,
                    'mapping.Bar',
                    ['java.lang.String', 'java.lang.Boolean'])
            ]
        ]
    }

    void "reads global response type mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0

map:
  responses:
    - content: application/vnd.array => java.util.List
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def response = mappings.first () as ResponseTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypeNames == []
    }

    void "reads global parameter type mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  parameters:
    - name: foo => mapping.Foo
    - add: bar => mapping.Bar
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 2

        def parameter = mappings.first () as ParameterTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'mapping.Foo'
        parameter.mapping.genericTypeNames == []

        def additional = mappings[1] as AddParameterTypeMapping
        additional.parameterName == 'bar'
        additional.mapping.sourceTypeName == null
        additional.mapping.sourceTypeFormat == null
        additional.mapping.targetTypeName == 'mapping.Bar'
        additional.mapping.genericTypeNames == []
    }

    void "reads endpoint exclude flag" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  paths:
    /foo:
      exclude: ${exclude.toString ()}
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.exclude == exclude
        endpoint.typeMappings.empty

        where:
        exclude << [true, false]
    }

    void "reads endpoint parameter type mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  paths:
    /foo:
      parameters:
        - name: foo => mapping.Foo
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1
        def parameter = endpoint.typeMappings.first () as ParameterTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'mapping.Foo'
        parameter.mapping.genericTypeNames == []
    }

    void "reads endpoint add mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  paths:
    /foo:
      parameters:
        - add: request => javax.servlet.http.HttpServletRequest
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def parameter = endpoint.typeMappings.first () as AddParameterTypeMapping
        parameter.parameterName == 'request'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'javax.servlet.http.HttpServletRequest'
        parameter.mapping.genericTypeNames == []
    }

    void "reads endpoint response type mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  paths:
    /foo:
      responses:
        - content: application/vnd.array => java.util.List
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def response = endpoint.typeMappings.first () as ResponseTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypeNames == []
    }

    void "reads global result mapping #result" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  result: $result
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as ResultTypeMapping
        type.targetTypeName == expected

        where:
        result                                    | expected
        'plain'                                   | 'plain'
        'org.springframework.http.ResponseEntity' | 'org.springframework.http.ResponseEntity'
    }

    void "reads endpoint result mapping #result" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  paths:
    /foo:
      result: $result
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def type = endpoint.typeMappings.first () as ResultTypeMapping
        type.targetTypeName == expected

        where:
        result                                    | expected
        'plain'                                   | 'plain'
        'org.springframework.http.ResponseEntity' | 'org.springframework.http.ResponseEntity'
    }

    void "reads global single & multi mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  single: $single
  multi: $multi
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 2
        def typeSingle = mappings.first () as TypeMapping
        typeSingle.sourceTypeName == 'single'
        typeSingle.targetTypeName == single
        def typeMulti = mappings[1] as TypeMapping
        typeMulti.sourceTypeName == 'multi'
        typeMulti.targetTypeName == multi

        where:
        single << ['reactor.core.publisher.Mono']
        multi << ['reactor.core.publisher.Flux']
    }

    void "reads endpoint single & multi mapping" () {
        String yaml = """\
openapi-processor-spring: v2.0
    
map:
  paths:
    /foo:
      single: $single
      multi: $multi
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 2

        def typeSingle = endpoint.typeMappings.first () as TypeMapping
        typeSingle.sourceTypeName == 'single'
        typeSingle.targetTypeName == single
        def typeMulti = endpoint.typeMappings[1] as TypeMapping
        typeMulti.sourceTypeName == 'multi'
        typeMulti.targetTypeName == multi

        where:
        single << ['reactor.core.publisher.Mono']
        multi << ['reactor.core.publisher.Flux']
    }

}
