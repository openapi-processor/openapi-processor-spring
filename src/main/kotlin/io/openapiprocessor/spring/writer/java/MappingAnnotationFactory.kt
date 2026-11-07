/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.MappingAnnotationFactory as CoreMappingAnnotationFactory

/**
 * spring mapping annotation factory
 */
class MappingAnnotationFactory(private val annotations: FrameworkAnnotations): CoreMappingAnnotationFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet> {
        return listOf(createAnnotation(endpoint, endpointResponse))
    }

    private fun createAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): CodeSnippet {
        val annotation = annotations.getAnnotation(endpoint.method)

        var snippet = annotation.annotationName
        snippet += "("
        snippet += "path = " + quote(endpoint.path)

        val consumes = endpoint.getConsumesContentTypes()
        if (consumes.isNotEmpty()) {
            snippet += ", "
            snippet += "consumes = {"
            snippet += consumes.joinToString(", ") {
                quote(it)
            }
            snippet += '}'
        }

        val produces = endpointResponse.contentTypes
        if (produces.isNotEmpty()) {
            snippet += ", "
            snippet += "produces = {"

            snippet += produces.joinToString(", ") {
                quote(it)
            }

            snippet += "}"
        }

        annotation.parameters.forEach {
            snippet += ", "
            snippet += "${it.key} = ${it.value.value}"
        }

        snippet += ")"

        return CodeSnippet(snippet, annotation.imports + annotation.referencedImports)
    }

    private fun quote(content: String): String {
        return '"' + content + '"'
    }
}
