/*
 * Copyright 2019-2020 the original authors
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

package com.github.hauner.openapi.processor

import com.github.hauner.openapi.api.OpenApiProcessor
import com.github.hauner.openapi.micronaut.processor.MicronautProcessor
import com.github.hauner.openapi.spring.parser.ParserType
import com.github.hauner.openapi.spring.processor.SpringProcessor

class TestSet {

    static def testSets = [
        'bean-validation',
        'endpoint-exclude',
        'method-operation-id',
        'no-response-content',
        'params-additional',
        'params-complex-data-types',
        'params-enum',
        'params-simple-data-types',
        'params-request-body',
        'params-request-body-multipart-form-data',
        'params-path-simple-data-types',
        'params-spring-pageable-mapping',
        'ref-into-another-file',
        'ref-loop',
        'response-array-data-type-mapping',
        'response-complex-data-types',
        'response-content-multiple',
        'response-content-single',
        'response-result-mapping',
        'response-simple-data-types',
        'response-single-multi-mapping',
        'schema-composed'
    ]

    static String DEFAULT_OPTIONS_SPRING = """\
openapi-processor-spring: v2

options:
  package-name: generated

map:  
  result: org.springframework.http.ResponseEntity  
    """

    static String DEFAULT_OPTIONS_MICRONAUT = """\
openapi-processor-spring: v2

options:
  package-name: generated

map:  
  result: org.springframework.http.ResponseEntity  
    """

    String name
    String target = 'spring'
    ParserType parser = ParserType.SWAGGER

    OpenApiProcessor getProcessor () {
        if  (target == 'micronaut') {
            new MicronautProcessor()
        } else {
            new SpringProcessor()
        }
    }

    String getDefaultOptions() {
        if (target == 'micronaut') {
            DEFAULT_OPTIONS_MICRONAUT
        } else {
            DEFAULT_OPTIONS_SPRING
        }
    }

    @Override
    String toString () {
        "${parser.name ().toLowerCase ()} - $name"
    }

}
