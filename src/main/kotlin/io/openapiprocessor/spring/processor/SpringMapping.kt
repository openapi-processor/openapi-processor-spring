/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

class SpringMappingOptionsValues(val annotations: String = "mapping")
class SpringMappingOptions(val spring: SpringMappingOptionsValues = SpringMappingOptionsValues())
class SpringMapping(val options: SpringMappingOptions)
