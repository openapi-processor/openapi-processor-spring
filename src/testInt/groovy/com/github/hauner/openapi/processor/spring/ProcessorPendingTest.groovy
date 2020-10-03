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

package com.github.hauner.openapi.processor.spring

import io.openapiprocessor.spring.processor.SpringProcessor
import com.github.hauner.openapi.test.TestSet
import io.openapiprocessor.core.parser.ParserType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

//@Ignore
@RunWith(Parameterized)
class ProcessorPendingTest extends EndToEndBase {

    @Parameterized.Parameters(name = "{0}")
    static Collection<TestSet> sources () {
        return [
            new TestSet(name: 'params-request-body-multipart-mapping', processor: new SpringProcessor (), parser: ParserType.SWAGGER),
//            new TestSet(name: 'params-simple-data-types-micronaut', processor: new SpringProcessor (), parser: ParserType.OPENAPI4J)
        ]
    }

    ProcessorPendingTest (TestSet testSet) {
        super (testSet)
    }

    @Test
    void "native - processor creates expected files for api set "() {
        runOnNativeFileSystem ()
    }

}
