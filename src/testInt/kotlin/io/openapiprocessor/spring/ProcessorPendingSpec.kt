/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType.*
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
    return listOf("reactive-response-types", "reactive-response-types2", "reactive-response-types3")
        .flatMap { testSet ->
            listOf(INTERNAL, OPENAPI4J, SWAGGER)
                .flatMap { parser ->
                    listOf(API_30, API_31)
                        .map { api ->
                            testSet(testSet, parser, api, model = "default", outputs = "outputs.yaml", expected = "outputs")
                        }
                }
        }
            .filter { it.parser != "swagger" && it.openapi != API_31 } // swagger parser in OpenAPI 3.1 generates classes with different names.

}
