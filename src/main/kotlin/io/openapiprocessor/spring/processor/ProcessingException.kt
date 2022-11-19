/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

class ProcessingException(ex: Exception): RuntimeException(ex) {

    override val message: String
        get() = "failed to run openapi-processor-spring!"
}
