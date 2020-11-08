/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.writer.java.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import java.io.Writer

/**
 * spring parameter annotation writer
 *
 * @author Martin Hauner
 */
class ParameterAnnotationWriter(
    private val annotations: FrameworkAnnotations

): CoreParameterAnnotationWriter {

    override fun write(target: Writer, parameter: Parameter) {
        if (parameter is RequestBody) {
            target.write(createRequestBodyAnnotation(parameter))
        } else {
            target.write(createParameterAnnotation(parameter))
        }
    }

    private fun createRequestBodyAnnotation(requestBody: RequestBody): String {
        var annotation = getAnnotationName(requestBody)

        // required is default, so add required only if the parameter is not required
        if (!requestBody.required) {
            annotation += "(required = false)"
        }

        return annotation
    }

    private fun createParameterAnnotation(parameter: Parameter): String {
        if (! parameter.withAnnotation) {
            return ""
        }

        var annotation = getAnnotationName (parameter)

        if (! parameter.withParameters) {
            return annotation
        }

        annotation += "("
        annotation += "name = " + quote (parameter.name)

        // required is the default, add required only if the parameter is not required
        if (!parameter.required) {
            annotation += ", "
            annotation += "required = false"
        }

        if (!parameter.required && hasDefault (parameter)) {
            annotation += ", "
            annotation += "defaultValue = ${getDefault(parameter)}"
        }

        annotation += ")"
        return annotation
    }

    private fun getAnnotationName(parameter: Parameter): String {
        return annotations.getAnnotation (parameter).annotationName
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
