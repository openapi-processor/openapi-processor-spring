/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.model

import io.openapiprocessor.core.model.APPLICATION_FORM_URLENCODED
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.RequestBody as RequestBodyCore

class RequestBody(
    name: String,
    contentType: String,
    dataType: DataType,
    required: Boolean = false,
    deprecated: Boolean = false,
    description: String? = null
) : RequestBodyCore(name, contentType, dataType, required, deprecated, description) {

    /**
     * controls if a _request body_ parameter should have a {@code @RequestBody} annotation.
     */
    override val withAnnotation: Boolean
        get() {
            return contentType != APPLICATION_FORM_URLENCODED
        }
}
