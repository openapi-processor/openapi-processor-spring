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

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.generatr.mapping.ArrayTypeMapping
import com.github.hauner.openapi.spring.generatr.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.generatr.mapping.ResponseTypeMapping
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

//@Ignore
@RunWith(Parameterized)
class GeneratrPendingTest extends GeneratrTestBase {

    @Parameterized.Parameters(name = "{0}")
    static Collection<TestSet> sources () {
        return [
//            new TestSet(data: 'params-complex-data-types')
            new TestSet(name: 'response-array-data-type-mapping',
                options: new ApiOptions(
                    typeMappings: [
                        new ArrayTypeMapping(
                            targetTypeName: 'java.util.Collection'
                        ),
                        new ResponseTypeMapping (
                            contentType: 'application/vnd.global-response',
                            sourceTypeName: 'array',
                            targetTypeName: 'java.util.List'
                        ),
                        new EndpointTypeMapping (
                            path: '/array-endpoint-response',
                            typeMappings: [
                                new ResponseTypeMapping (
                                    contentType: 'application/vnd.any',
                                    sourceTypeName: 'array',
                                    targetTypeName: 'java.util.Set')]
                        )
                    ]
                )
            )
        ]
    }

    GeneratrPendingTest (TestSet testSet) {
        super (testSet)
    }

}
