/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory as CoreMappingAnnotationFactory

/**
 * spring mapping annotation factory
 */
class MappingAnnotationFactory(private val annotations: SpringFrameworkAnnotations): CoreMappingAnnotationFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<String> {
        return listOf(createAnnotation(endpoint, endpointResponse))
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

        val produces = endpointResponse.contentTypes
        if (produces.isNotEmpty()) {
            mapping += ", "
            mapping += "produces = {"

            mapping += produces.joinToString(", ") {
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
