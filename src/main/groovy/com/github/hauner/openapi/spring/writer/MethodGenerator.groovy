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

import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.RequestBody
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType

import com.github.hauner.openapi.spring.model.parameters.Parameter
import com.github.hauner.openapi.support.Identifier
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ArrayTypeName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.WildcardTypeName

import javax.lang.model.element.Modifier

/**
 * Writer for Java interface methods, i.e. endpoints.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class MethodGenerator {

    ApiOptions apiOptions
    BeanValidationGenerator beanValidationFactory

    MethodSpec generateMethodSpec(Endpoint endpoint) {
        def methodSpecBuilder = MethodSpec.methodBuilder (createMethodName (endpoint))
            .addModifiers (Modifier.PUBLIC, Modifier.ABSTRACT)
            .addAnnotation (createMappingAnnotation (endpoint))
            .addParameters (createParameters(endpoint))
            .returns (generateReturnType (endpoint))

        methodSpecBuilder.build ()
    }

    private ParameterizedTypeName generateReturnType (Endpoint endpoint) {
        if(!endpoint.hasMultiStatusResponses ()){
            ParameterizedTypeName.get (
                ClassName.get ('org.springframework.http', 'ResponseEntity'),
                createTypeName(endpoint.singleResponse.responseType)
            )
        } else {
            ParameterizedTypeName.get (
                ClassName.get ('org.springframework.http', 'ResponseEntity'),
                WildcardTypeName.subtypeOf (Object.class)
            )
        }
    }

    private TypeName createTypeName (DataType dataType) {
        if(dataType.generics.empty){
            return ClassName.get (dataType.packageName, dataType.name)
        }

        if(ArrayDataType.isArray (dataType)){
            return ArrayTypeName.of (createTypeName (dataType.generics[0]))
        }

        TypeName[] genericTypeNames =  dataType.generics.collect {
            createTypeName (it)
        }.toArray (new TypeName[0])

        return ParameterizedTypeName.get (ClassName.get (dataType.packageName, dataType.name), genericTypeNames)
    }

    private Iterable<ParameterSpec> createParameters (Endpoint endpoint) {
        List<ParameterSpec> parameterSpecs = []

        endpoint.parameters.each {
            def parameterSpecBuilder = ParameterSpec.builder (
                createTypeName (it.dataType),
                Identifier.toCamelCase (it.name)
            )

            if (apiOptions.beanValidation) {
                parameterSpecBuilder.addAnnotations (beanValidationFactory.generateAnnotations (it.dataType))
            }

            if (it.withAnnotation ()) {
                parameterSpecBuilder.addAnnotation (createParameterAnnotation (it))
            }

            parameterSpecs.add (parameterSpecBuilder.build ())
        }

        if (!endpoint.requestBodies.empty) {
            def body = endpoint.requestBody

            def parameterSpecBuilder = ParameterSpec.builder (
                createTypeName (body.requestBodyType),
                'body'
            )

            if (apiOptions.beanValidation) {
                parameterSpecBuilder.addAnnotations(beanValidationFactory.generateAnnotations (body.requestBodyType))
            }

            parameterSpecBuilder.addAnnotation (createRequestBodyAnnotation(body))
            parameterSpecs.add (parameterSpecBuilder.build ())
        }

        parameterSpecs
    }

    private AnnotationSpec createMappingAnnotation (Endpoint endpoint) {
        def mappingAnnotationSpecBuilder = AnnotationSpec
            .builder (ClassName.get (endpoint.method.package, endpoint.method.className))
            .addMember ('path', '$S', endpoint.path)

        if (!endpoint.requestBodies.empty) {
            mappingAnnotationSpecBuilder.addMember ('consumes', '{$S}', endpoint.requestBody.contentType)
        }

        if (endpoint.hasResponseContentTypes ()) {

            String format = '{'
            for (int i = 0; i < endpoint.responseContentTypes.size (); i++) {
                if(i != 0){
                    format += ', '
                }
                format += '$S'
            }
            format += '}'

            mappingAnnotationSpecBuilder.addMember ('produces', format, endpoint.responseContentTypes.toArray ())
        }

        mappingAnnotationSpecBuilder.build ()
    }

    private AnnotationSpec createParameterAnnotation (Parameter parameter) {
        def annotationBuilder = AnnotationSpec.builder (ClassName.get (parameter.package, parameter.annotationName))

        if (parameter.withParameters ()) {
            annotationBuilder.addMember ('name', '$S', parameter.name)

            // required is default, so add required only if the parameter is not required
            if (!parameter.required) {
                annotationBuilder.addMember ('required', '$L', false)
            }

            if (!parameter.required && parameter.constraints?.hasDefault()) {
                def defaultValue = parameter.constraints.default
                if(defaultValue instanceof String) {
                    annotationBuilder.addMember ('defaultValue', '$S', defaultValue)
                } else {
                    annotationBuilder.addMember ('defaultValue', '$L', defaultValue)
                }
            }
        }

        annotationBuilder.build ()
    }

    private AnnotationSpec createRequestBodyAnnotation (RequestBody requestBody) {
        def annotationBuilder = AnnotationSpec.builder (ClassName.get (requestBody.package, requestBody.annotationName))

        // required is default, so add required only if the parameter is not required
        if (!requestBody.required) {
            annotationBuilder.addMember ('required', '$L', false)
        }

        annotationBuilder.build ()
    }


    private String createMethodName (Endpoint endpoint) {
        def tokens = endpoint.path.tokenize ('/')
        tokens = tokens.collect { Identifier.toCamelCase (it).capitalize () }
        def name = tokens.join ('')
        "${endpoint.method.method}${name}"
    }
}
