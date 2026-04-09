/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.processor.JsonSchema
import java.net.URI

const val SPRING_MAPPING_SCHEMA_VERSION = "v1"

val JSON_SCHEMA_SPRING = JsonSchema(
    URI("https://openapiprocessor.io/schemas/mapping/spring-${SPRING_MAPPING_SCHEMA_VERSION}.json"),
    "/mapping/${SPRING_MAPPING_SCHEMA_VERSION}/spring-mapping.yaml.json")
