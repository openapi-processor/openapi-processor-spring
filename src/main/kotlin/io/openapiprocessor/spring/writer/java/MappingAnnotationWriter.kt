/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import java.io.Writer
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter

/**
 * spring mapping annotation writer
 */
class MappingAnnotationWriter(private val annotations: SpringFrameworkAnnotations): CoreMappingAnnotationWriter {

    override fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        target.write(createAnnotation(endpoint, endpointResponse))
    }

    private fun createAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val annotation = annotations.getAnnotation(endpoint.method)

        var mapping = annotation.annotationName
        mapping += "("
        mapping += "path = " + quote(endpoint.path)

        val consumes = endpoint.getConsumesContentTypes()
        if (consumes.isNotEmpty()) {
            mapping += ", "
            mapping += "consumes = {"
            mapping += consumes.joinToString(", ") {
                quote(it)
            }
            mapping += '}'
        }

        val contentTypes = endpointResponse.contentTypes
        if (contentTypes.isNotEmpty()) {
            mapping += ", "
            mapping += "produces = {"

            mapping += contentTypes.joinToString(", ") {
                quote(it)
            }

            mapping += "}"
        }

        annotation.parameters.forEach {
            mapping += ", "
            mapping += "${it.key} = ${it.value.value}"
        }

        mapping += ")"
        return mapping
    }

    private fun quote(content: String): String {
        return '"' + content + '"'
    }
}
