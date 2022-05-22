/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

data class TestSet(val name: String, val openapi: String)

val ALL_30: List<TestSet> = listOf(
    TestSet("endpoint-http-mapping", API_30),
    TestSet("params-complex-data-types", API_30),
    TestSet("params-pageable-mapping", API_30),
    TestSet("params-path-simple-data-types", API_30),
    TestSet("params-query-annotate-simple-mapping", API_30),
    TestSet("params-request-body", API_30),
    TestSet("params-request-body-multipart-mapping", API_30),
    TestSet("params-simple-data-types", API_30)
)

val ALL_31: List<TestSet> = listOf(
    TestSet("endpoint-http-mapping", API_31),
    TestSet("params-complex-data-types", API_31),
    TestSet("params-pageable-mapping", API_31),
    TestSet("params-path-simple-data-types", API_31),
    TestSet("params-query-annotate-simple-mapping", API_31),
    TestSet("params-request-body", API_31),
    TestSet("params-request-body-multipart-mapping", API_31),
    TestSet("params-simple-data-types", API_31)
)
