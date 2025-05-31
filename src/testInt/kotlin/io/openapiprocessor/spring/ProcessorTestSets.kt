/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring

import io.openapiprocessor.test.*

val ALL_30: List<TestParams> = listOf(
    test30_D_("endpoint-http-mapping"),
    test30_DR("params-complex-data-types"),
    test30_D_("params-enum"),
    test30_D_("params-pageable-mapping"),
    test30_D_("params-path-simple-data-types"),
    test30_D_("params-query-annotate-simple-mapping"),
    test30_DR("params-request-body"),
    test30_D_("params-request-body-multipart-mapping"),
    test30_D_("params-simple-data-types"),
    test30_D_("response-status")
)

val ALL_31: List<TestParams> = listOf(
    test31_D_("endpoint-http-mapping"),
    test31_DR("params-complex-data-types"),
    test31_D_("params-enum"),
    test31_D_("params-pageable-mapping"),
    test31_D_("params-path-simple-data-types"),
    test31_D_("params-query-annotate-simple-mapping"),
    test31_DR("params-request-body"),
    test31_DR("params-request-body-multipart-mapping"),
    test31_D_("params-simple-data-types"),
    test31_D_("response-status")
)
