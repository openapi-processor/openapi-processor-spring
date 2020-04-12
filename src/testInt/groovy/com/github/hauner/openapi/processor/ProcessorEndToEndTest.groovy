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

package com.github.hauner.openapi.processor

import com.github.hauner.openapi.spring.parser.ParserType
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * using Junit so IDEA adds a "<Click to see difference>" when using assertEquals().
 */

@RunWith(Parameterized)
class ProcessorEndToEndTest extends ProcessorTestBase {

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

    @Parameterized.Parameters(name = "{0}")
    static Collection<TestSet> sources () {
        def swagger = testSets.collect {
           new TestSet (name: it, parser: ParserType.SWAGGER)
        }

        def openapi4j = testSets.collect {
           new TestSet (name: it, parser: ParserType.OPENAPI4J)
        }

        swagger + openapi4j
    }

    ProcessorEndToEndTest (TestSet testSet) {
        super (testSet)
    }

}
