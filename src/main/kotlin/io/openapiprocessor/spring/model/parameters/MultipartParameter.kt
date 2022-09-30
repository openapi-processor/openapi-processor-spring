/*
 * Copyright 2021 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.model.parameters

import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.MultipartParameter as CoreMultipartParameter

class MultipartParameter(
    name: String,
    dataType: DataType,
    required: Boolean = false,
    deprecated: Boolean = false,
    description: String? = null,
    val contentType: String? = null
): CoreMultipartParameter(name, dataType, required, deprecated, description)
