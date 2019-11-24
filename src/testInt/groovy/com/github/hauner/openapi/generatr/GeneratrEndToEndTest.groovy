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

package com.github.hauner.openapi.generatr

import org.junit.runner.RunWith
import org.junit.runners.Parameterized


/**
 * using Junit so IDEA adds a "<Click to see difference>" when using assertEquals().
 */

@RunWith(Parameterized)
class GeneratrEndToEndTest extends GeneratrTestBase {

    @Parameterized.Parameters(name = "{0}")
    static Collection<TestSet> sources () {
        return [
            new TestSet(name: 'ref-into-another-file'),
            new TestSet(name: 'no-response-content'),
            new TestSet(name: 'response-simple-data-types'),
            new TestSet(name: 'response-complex-data-types'),
            new TestSet(name: 'response-array-data-type-mapping'),
            new TestSet(name: 'params-simple-data-types'),
            new TestSet(name: 'params-path-simple-data-types'),
            new TestSet(name: 'params-request-body'),
            new TestSet(name: 'params-spring-pageable-mapping')
        ]
    }

    GeneratrEndToEndTest (TestSet testSet) {
        super (testSet)
    }

}
