/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.openapiprocessor.spring.processor.SpringServiceV2
import io.openapiprocessor.test.*

val ALL_3x: List<TestParams2> = join(
    emptyList(),
    testX("endpoint-http-mapping"),
    tests("params-complex-data-types"),
    testX("params-enum"),
    testX("params-pageable-mapping"),
    testX("params-path-simple-data-types"),
    testX("params-query-annotate-simple-mapping"),
    tests("params-request-body"),
    testX("params-request-body-multipart-mapping"),
    testX("params-simple-data-types"),
    testX("response-status")
)

fun testSet(
    name: String,
    parser: String = "INTERNAL",
    openapi: String = "openapi.yaml",
    model: String = "default",
    inputs: String = "inputs.yaml",
    outputs: String = "outputs.yaml",
    expected: String = "outputs"
): TestSet {
    val testSet = TestSet()
    testSet.name = name
    testSet.processor = SpringServiceV2(testMode = true)
    testSet.parser = parser
    testSet.modelType = model
    testSet.openapi = openapi
    testSet.inputs = inputs
    testSet.outputs = outputs
    testSet.expected = expected
    return testSet
}

fun buildTestSets(): List<TestSet> {
    return ALL_3x
        .filter {
            when (it.parser) {
                "INTERNAL" -> {
                    true
                }
                "SWAGGER" if it.openapi == "openapi30.yaml" -> {
                    true
                }
                "OPENAPI4J" if it.openapi == "openapi30.yaml" -> {
                    true
                }
                else -> {
                    false
                }
            }
        }
        .map {
            testSet(
                it.name,
                it.parser,
                it.openapi,
                model = it.modelType,
                outputs = it.outputs,
                expected = it.expected
            )
        }
}
