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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.core.framework.FrameworkAnnotations
import com.github.hauner.openapi.core.model.RequestBody
import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.core.writer.ParameterAnnotationWriter as CoreParameterAnnotationWriter

/**
 * spring parameter annotation writer
 *
 * @author Martin Hauner
 */
class ParameterAnnotationWriter implements CoreParameterAnnotationWriter {
    FrameworkAnnotations annotations

    @Override
    void write (Writer target, Parameter parameter) {
        if (parameter instanceof RequestBody) {
            target.write (createAnnotation (parameter as RequestBody))
        } else {
            target.write (createAnnotation (parameter))
        }
    }

    private String createAnnotation (RequestBody requestBody) {
        String annotation = getAnnotationName (requestBody)

        // required is default, so add required only if the parameter is not required
        if (!requestBody.required) {
            annotation += '(required = false)'
        }

        annotation
    }

    private String createAnnotation (Parameter parameter) {
        if (! parameter.withAnnotation ()) {
            return ""
        }

        String annotation = getAnnotationName (parameter)

        if (! parameter.withParameters ()) {
            return annotation
        }

        annotation += '('
        annotation += 'name = ' + quote (parameter.name)

        // required is the default, add required only if the parameter is not required
        if (!parameter.required) {
            annotation += ", "
            annotation += 'required = false'
        }

        if (!parameter.required && hasDefault (parameter)) {
            annotation += ", "
            annotation += "defaultValue = ${getDefault(parameter)}"
        }

        annotation += ')'
        annotation
    }

    private String getAnnotationName (Parameter parameter) {
        annotations.getAnnotation (parameter).annotationName
    }

    private boolean hasDefault (Parameter parameter) {
        parameter.constraints?.hasDefault()
    }

    private String getDefault (Parameter parameter) {
        quote(parameter.constraints.default as String)
    }

    private String quote (String content) {
        '"' + content + '"'
    }

}
