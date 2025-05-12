/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.model.EndpointResponseStatus
import java.io.Writer
import io.openapiprocessor.core.writer.java.StatusAnnotationWriter as CoreStatusAnnotationWriter

class StatusAnnotationWriter(private val annotations: FrameworkAnnotations): CoreStatusAnnotationWriter {

    override fun write(
        target: Writer,
        endpoint: Endpoint,
        endpointResponse: EndpointResponse
    ) {
        target.write(createStatusAnnotation(endpointResponse))
    }

    private fun createStatusAnnotation(status: EndpointResponseStatus): String {
        val data = annotations.getAnnotation(status)
        val statusCode = status.statusCode.toInt()

        var annotation = data.annotationName
        annotation += "("
        annotation += "HttpStatus.valueOf($statusCode)"
        annotation += ")"
        return annotation
    }
}
