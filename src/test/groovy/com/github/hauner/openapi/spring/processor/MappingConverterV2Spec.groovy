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
            ]
        ]
    }

}
