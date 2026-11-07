/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.CodeSnippet
import io.openapiprocessor.core.writer.java.ParameterAnnotationFactory as CoreFactory

/**
 * Spring parameter annotation writer
 */
class ParameterAnnotationFactory(private val annotations: FrameworkAnnotations): CoreFactory {

    override fun create(parameter: Parameter): List<CodeSnippet> {
        val codeSnippet = if (parameter is RequestBody) {
            createRequestBodyCodeSnippet(parameter)
        } else {
            createParameterCodeSnippet(parameter)
        }

        return listOfNotNull(codeSnippet)
    }

    private fun createRequestBodyCodeSnippet(requestBody: RequestBody): CodeSnippet? {
        if (! requestBody.withAnnotation) {
            return null
        }

        val annotation = annotations.getAnnotation (requestBody)

        var snippet = annotation.annotationName

        // required is default, so add required only if the parameter is not required
        if (!requestBody.required) {
            snippet += "(required = false)"
        }

        return CodeSnippet(snippet, annotation.imports)
    }

    private fun createParameterCodeSnippet(parameter: Parameter): CodeSnippet? {
        if (! parameter.withAnnotation) {
            return null
        }

        val annotation = annotations.getAnnotation (parameter)


        var snippet = annotation.annotationName
        if (parameter.withParameters) {
            snippet += "("
            snippet += "name = " + quote (parameter.name)

            // required is the default, add required only if the parameter is not required
            if (!parameter.required) {
                snippet += ", "
                snippet += "required = false"
            }

            if (!parameter.required && hasDefault (parameter)) {
                snippet += ", "
                snippet += "defaultValue = ${getDefault(parameter)}"
            }

            snippet += ")"
        }

        return CodeSnippet(snippet, annotation.imports)
    }

    private fun hasDefault(parameter: Parameter): Boolean {
        return parameter.constraints.hasDefault()
    }

    private fun getDefault(parameter: Parameter): String {
        return quote(parameter.constraints.default.toString())
    }

    private fun quote(content: String): String {
        return '"' + content + '"'
    }

}
