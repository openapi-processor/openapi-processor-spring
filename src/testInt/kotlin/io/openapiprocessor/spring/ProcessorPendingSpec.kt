/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType.INTERNAL
import io.openapiprocessor.test.FileSupport
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner

/**
 * helper to run selected integration tests.
 */
class ProcessorPendingSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = false) {
            val folder = tempdir()

            val support = FileSupport(
                ProcessorPendingSpec::class.java,
                testSet.inputs, testSet.outputs)

            TestSetRunner(testSet, support)
            .runOnNativeFileSystem(folder)
            .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    return listOf(
        testSet("params-request-body-multipart-mapping", INTERNAL, API_30),
        testSet("params-request-body-multipart-mapping", INTERNAL, API_31)
    )
}
