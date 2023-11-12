/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType.INTERNAL
import io.openapiprocessor.test.*
import io.openapiprocessor.test.API_30
import io.openapiprocessor.test.TestSet

/**
 * helper to run selected integration tests.
 */
class ProcessorPendingSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = true) {
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
        testSet("params-enum", INTERNAL, API_30, model = "default", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("params-request-body-multipart-mapping", INTERNAL, API_30, model = "default", outputs = "outputs.yaml", expected = "outputs"),
//        testSet("params-request-body-multipart-mapping", INTERNAL, API_31, model = "default", outputs = "outputs.yaml", expected = "outputs")
    )
}
