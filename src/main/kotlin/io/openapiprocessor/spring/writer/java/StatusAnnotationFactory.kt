/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.StatusAnnotationFactory as CoreFactory

class StatusAnnotationFactory(private val annotations: FrameworkAnnotations): CoreFactory {

    override fun create(endpoint: Endpoint, endpointResponse: EndpointResponse): List<CodeSnippet> {
        val annotation = annotations.getAnnotation(endpointResponse)
        val status = annotation.parameters["code"]
        return listOf(CodeSnippet(
            "${annotation.annotationName}(${status!!.value})",
            annotation.imports + annotation.referencedImports))
    }
}
