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

import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.core.writer.MappingAnnotationWriter as CoreMappingAnnotationWriter
import com.github.hauner.openapi.core.writer.ParameterAnnotationWriter as CoreParameterAnnotationWriter
import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.EndpointResponse
import com.github.hauner.openapi.spring.model.RequestBody
import com.github.hauner.openapi.support.Identifier

/**
 * Writer for Java interface methods, i.e. endpoints.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class MethodWriter {

    ApiOptions apiOptions
    CoreMappingAnnotationWriter mappingAnnotationWriter
    CoreParameterAnnotationWriter parameterAnnotationWriter
    BeanValidationFactory beanValidationFactory

    void write (Writer target, Endpoint endpoint, EndpointResponse endpointResponse) {
        target.write ("""\
    ${createMappingAnnotation (endpoint, endpointResponse)}
    ${endpointResponse.responseType} ${createMethodName (endpoint, endpointResponse)}(${createParameters(endpoint)});
""")
    }

    private String createMappingAnnotation (Endpoint endpoint, EndpointResponse endpointResponse) {
        def annotation = new StringWriter ()
        mappingAnnotationWriter.write (annotation, endpoint, endpointResponse)
        annotation.toString ()
    }

    private String createParameterAnnotation (Parameter parameter) {
        def annotation = new StringWriter ()
        parameterAnnotationWriter.write (annotation, parameter)
        annotation.toString ()
    }

    private String createRequestBodyAnnotation (RequestBody requestBody) {
        String param = "${requestBody.annotation}"

        // required is default, so add required only if the parameter is not required
        if (!requestBody.required) {
            param += '(required = false)'
        }

        param
    }

    private String createMethodName (Endpoint endpoint, EndpointResponse endpointResponse) {
        if (endpoint.operationId != null) {
            return Identifier.toCamelCase (endpoint.operationId)
        }

        def tokens = endpoint.path.tokenize ('/')

        if (endpoint.hasMultipleEndpointResponses ()) {
            tokens += endpointResponse.contentType.tokenize ('/')
        }

        tokens = tokens.collect { Identifier.toCamelCase (it).capitalize () }
        def name = tokens.join ('')
        "${endpoint.method.method}${name}"
    }

    private String createParameters (Endpoint endpoint) {
        def ps = endpoint.parameters.collect {

            def methodDefinition = ''

            if (apiOptions.beanValidation) {
                methodDefinition += " ${beanValidationFactory.createAnnotations (it.dataType)}"
            }

            if (it.withAnnotation ()) {
                methodDefinition += " ${createParameterAnnotation (it)}"
            }

            methodDefinition += " ${it.dataType.name} ${Identifier.toCamelCase (it.name)}"
            methodDefinition.trim()
        }

        if (!endpoint.requestBodies.empty) {
            def body = endpoint.requestBody
            def beanValidationAnnotations = ''
            if (apiOptions.beanValidation) {
                beanValidationAnnotations += " ${beanValidationFactory.createAnnotations (body.requestBodyType)}"
            }
            def param = "${beanValidationAnnotations} ${createRequestBodyAnnotation(body)} ${body.requestBodyType.name} body"
            ps.add (param.trim())
        }

        ps.join (', ')
    }

}
