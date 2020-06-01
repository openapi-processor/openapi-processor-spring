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

package com.github.hauner.openapi.core.processor

import spock.lang.Specification
import spock.lang.Subject

class MappingExampleV2Spec extends Specification {

    @Subject
    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()


    String yaml = """
openapi-processor-spring: v2

options:
    package-name: com.github.hauner.openapi
    bean-validation: true 

map:
  result: plain
  single: reactor.core.publisher.Mono
  multi: reactor.core.publisher.Flux

  types:
    - type: array => java.util.Collection

    - type: Schema => java.util.Map
      generics:
        - java.lang.String
        - java.lang.Double

  parameters:
    - name: foo => java.util.List
    - name: bar => com.github.hauner.openapi.Bar  

  responses:
    - content: application/vnd.something => java.util.List
    - content: application/json => com.github.hauner.openapi.FooBar  

  paths:
    /first:
      exclude: true

    /second:
      result: org.springframework.http.ResponseEntity
      single: reactor.core.publisher.Mono
      multi: reactor.core.publisher.Flux

      types:
        - type: Schema => java.util.Collection

      parameters:
        - name: foo => java.util.List
        - add: bar => java.util.Set

      responses:
        - content: application/vnd.any => java.util.Set
        - content: application/json => java.util.Map
"""

    void "parses mapping yaml" () {
        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings
    }

}
