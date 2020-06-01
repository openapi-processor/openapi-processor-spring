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

/**
 * obsolete
 */
class MappingExampleSpec extends Specification {

    @Subject
    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()


    String yaml = """
options:
    package-name: com.github.hauner.openapi
    bean-validation: true 

map:
  result: 
    to: plain

  types:
    - from: single
      to: reactor.core.publisher.Mono

    - from: multi
      to: reactor.core.publisher.Flux

    - from: array
      to: java.util.Collection

    - from: Schema
      to: java.util.Map
      generics:
        - java.lang.String
        - java.lang.Double

  result: 
    to: plain

  parameters:
    - name: foo
      to: java.util.List
      
    - name: bar
      to: com.github.hauner.openapi.Bar  

  responses:
    - content: application/vnd.something
      to: java.util.List

    - content: application/json
      to: com.github.hauner.openapi.FooBar  

  paths:
    /first:
      exclude: true

    /second:
      result:
        to: org.springframework.http.ResponseEntity

      types:
        - from: single
          to: reactor.core.publisher.Mono
      
        - from: Schema
          to: java.util.Collection

      result:
        to: org.springframework.http.ResponseEntity

      parameters:
        - name: foo
          to: java.util.List

        - add: bar
          as: java.util.Set

      responses:
        - content: application/vnd.any
          to: java.util.Set

        - content: application/json
          to: java.util.Map
"""

    void "parses mapping yaml" () {
        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings
    }

}
