/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.*
import io.openapiprocessor.test.TestSet

/**
 * runs integration tests with Jimfs.
 */
class ProcessorEndToEndJimfsSpec: StringSpec({

    for (testSet in sources()) {
        "jimfs - $testSet".config(enabled = true) {
            val fs = Jimfs.newFileSystem (Configuration.unix ())
            val reader = ResourceReader(ProcessorEndToEndJimfsSpec::class.java)

            val testFiles = TestFilesJimfs(fs, reader)
            val test = Test(testSet, testFiles)

            TestSetRunner(test, testSet)
                .run()
                .shouldBeTrue()
        }
    }

})

private fun sources(): Collection<TestSet> {
    // the swagger parser does not work with a custom FileSystem

    val openapi4j = ALL_30.map {
        testSet(it.name, ParserType.OPENAPI4J, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val openapi30 = ALL_30.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val openapi31 = ALL_31.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

    val openapi30r = ALL_30.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    val openapi31r = ALL_31.filter { it.modelTypes.contains(ModelTypes.RECORD) }.map {
        testSet(it.name, ParserType.INTERNAL, it.openapi, model = "record", outputs = it.outputs, expected = it.expected)
    }

    return openapi4j + openapi30 + openapi31 + openapi30r + openapi31r
}
