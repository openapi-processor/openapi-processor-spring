/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.booleans.shouldBeTrue
import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.test.FileSupport
import io.openapiprocessor.test.ModelTypes
import io.openapiprocessor.test.TestSet
import io.openapiprocessor.test.TestSetRunner

/**
 * run end to end integration test.
 */
class ProcessorEndToEndSpec: StringSpec({

    for (testSet in sources()) {
        "native - $testSet".config(enabled = true) {
            val folder = tempdir()

            val support = FileSupport(
                ProcessorEndToEndSpec::class.java,
                testSet.inputs, testSet.outputs)

            TestSetRunner(testSet, support)
            .runOnNativeFileSystem(folder)
            .shouldBeTrue()
        }
    }
})

private fun sources(): Collection<TestSet> {
    val swagger = ALL_30.map {
        testSet(it.name, ParserType.SWAGGER, it.openapi, model = "default", outputs = it.outputs, expected = it.expected)
    }

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

    return swagger + openapi4j + openapi30 + openapi31 + openapi30r + openapi31r
}
