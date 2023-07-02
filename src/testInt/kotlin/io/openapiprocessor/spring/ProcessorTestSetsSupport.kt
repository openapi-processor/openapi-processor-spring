/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.openapiprocessor.core.parser.ParserType
import io.openapiprocessor.spring.processor.SpringServiceV2
import io.openapiprocessor.test.TestSet

@Suppress("SameParameterValue")
fun testSet(
    name: String,
    parser: ParserType,
    openapi: String = "openapi.yaml",
    model: String = "default",
    inputs: String = "inputs.yaml",
    outputs: String = "generated.yaml",
    expected: String = "generated"
): TestSet {
    val testSet = TestSet()
    testSet.name = name
    testSet.processor = SpringServiceV2(testMode = true)
    testSet.parser = parser.name
    testSet.modelType = model
    testSet.openapi = openapi
    testSet.inputs = inputs
    testSet.outputs = outputs
    testSet.expected = expected
    return testSet
}
