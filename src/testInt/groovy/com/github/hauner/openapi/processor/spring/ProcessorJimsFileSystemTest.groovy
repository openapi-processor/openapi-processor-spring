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

package com.github.hauner.openapi.processor.spring

import io.openapiprocessor.spring.processor.SpringProcessor
import com.github.hauner.openapi.test.TestSet
import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.TestSetRunner
import spock.lang.Unroll

/**
 * run integration tests with Jimfs.
 */
class ProcessorJimsFileSystemTest extends EndToEndBase {

    static Collection<TestSet> sources () {
        // the swagger parser does not work with a custom FileSystem so we just run the test with
        // openapi4j

        TestSets.ALL.collect {
           new TestSet (name: it, processor: new SpringProcessor(), parser: ParserType.OPENAPI4J)
        }
    }

    @Unroll
    void "jimfs - #testSet"() {
        def runner = new TestSetRunner (testSet)
        def success = runner.runOnCustomFileSystem (Jimfs.newFileSystem (Configuration.unix ()))

        expect:
        assert success: "** found differences! **"

        where:
        testSet << sources ()
    }

}
