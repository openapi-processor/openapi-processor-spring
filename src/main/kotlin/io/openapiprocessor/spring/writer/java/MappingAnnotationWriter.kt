/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.support.capitalizeFirstChar
import java.io.Writer
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter

/**
 * spring mapping annotation writer
 */
class MappingAnnotationWriter: CoreMappingAnnotationWriter {

    override fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        target.write(createAnnotation(endpoint, endpointResponse))
    }

    private fun createAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        var mapping = getMappingAnnotation(endpoint)
        mapping += "("
        mapping += "path = " + quote(endpoint.path)

        val consumes = endpoint.getConsumesContentTypes()
        if (consumes.isNotEmpty()) {
            mapping += ", "
            mapping += "consumes = {"
            mapping +=  consumes.map {
                quote(it)
            }.joinToString(", ")
            mapping += '}'
        }

        val contentTypes = endpointResponse.contentTypes
        if (contentTypes.isNotEmpty()) {
            mapping += ", "
            mapping += "produces = {"

            mapping += contentTypes.map {
                quote (it)
            }.joinToString (", ")

            mapping += "}"
        }

        val method = endpoint.method.method
        if (method in arrayOf("head","trace","options")) {
            mapping += ", "
            mapping += "method = RequestMethod.${method.uppercase()}"
        }

        mapping += ")"
        return mapping
    }

    private fun getMappingAnnotation(endpoint: Endpoint): String {
        return when (endpoint.method) {
            HttpMethod.HEAD, HttpMethod.OPTIONS, HttpMethod.TRACE -> "@RequestMapping"
            else -> {
                "@${endpoint.method.method.capitalizeFirstChar()}Mapping"
            }
        }
    }

    private fun quote(content: String): String {
        return '"' + content + '"'
    }

}
