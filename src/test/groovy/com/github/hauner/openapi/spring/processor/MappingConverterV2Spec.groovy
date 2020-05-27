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

import com.github.hauner.openapi.spring.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
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

}
