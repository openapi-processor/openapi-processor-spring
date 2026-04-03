/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory as CoreMappingAnnotationFactory

/**
 * spring exchange annotation factory.
 * org.springframework.web.service.annotation.HttpExchange was designed to be neutral to client vs server use.
 * The contentType attribute is for the request body, while the accept attribute is for the server response.
 *
 * | attribute | client side (request) | server side (controller, response) |
 * |-----------|-----------------------|------------------------------------|
 * |contentType| sends header          | validates header                   |
 * |accept     | sends header          | sets contentType (on response)     |
 */
class ExchangeAnnotationFactory(private val annotations: FrameworkAnnotations): CoreMappingAnnotationFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<String> {
        return listOf(createAnnotation(endpoint, endpointResponse))
    }

    private fun createAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        val annotation = annotations.getAnnotation(endpoint.method)

        var mapping = annotation.annotationName
        mapping += "("
        mapping += "url = " + quote(endpoint.path)

        // todo warn if size is larger than 1?
        val consumes = endpoint.getConsumesContentTypes()
        if (consumes.isNotEmpty()) {
            mapping += ", "
            mapping += "contentType = "
            mapping += quote(consumes.first())
        }

        val produces = endpointResponse.contentTypes
        if (produces.isNotEmpty()) {
            mapping += ", "
            mapping += "accept = {"

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
