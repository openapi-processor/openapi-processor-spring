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
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.support.Identifier
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec

import javax.lang.model.element.Modifier

/**
 * Writer for POJO classes.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class DataTypeWriter {
    ApiOptions apiOptions
    BeanValidationFactory beanValidationFactory

    TypeSpec generateTypeSpec (ObjectDataType objectDataType) {
        def typeSpecBuilder = TypeSpec.classBuilder (objectDataType.type)
            .addModifiers (Modifier.PUBLIC)

        objectDataType.properties.keySet ().each {
            def propDataType = objectDataType.getObjectProperty (it)
            def propertyClassName = ClassName.get (propDataType.packageName, propDataType.name)
            def javaPropertyName = Identifier.toCamelCase (it)

            typeSpecBuilder
                .addField (generateFieldSpec (it, propDataType, javaPropertyName, propertyClassName))
                .addMethod (generateGetterMethodSpec (javaPropertyName, propertyClassName))
                .addMethod (generateSetterMethodSpec (javaPropertyName, propertyClassName))
        }

        typeSpecBuilder.build ()
    }

    private MethodSpec generateGetterMethodSpec (String propertyName, ClassName propertyClassName) {
        MethodSpec.methodBuilder ("get${propertyName.capitalize ()}")
            .addModifiers (Modifier.PUBLIC)
            .returns (propertyClassName)
            .addStatement ("return ${propertyName}")
            .build ()
    }

    private MethodSpec generateSetterMethodSpec (String propertyName, ClassName propertyClassName) {
        MethodSpec.methodBuilder ("set${propertyName.capitalize ()}")
            .addModifiers (Modifier.PUBLIC)
            .addParameter (ParameterSpec.builder (propertyClassName, propertyName).build ())
            .addStatement ("this.${propertyName} = ${propertyName}")
            .build ()
    }

    private FieldSpec generateFieldSpec (String propertyName, DataType dataType, String javaPropertyName, ClassName propertyClassName) {
        def fieldBuilder = FieldSpec.builder (propertyClassName, javaPropertyName, Modifier.PRIVATE)
            .addAnnotation (
                AnnotationSpec.builder (
                    ClassName.get ('com.fasterxml.jackson.annotation', 'JsonProperty')
                )
                .addMember ('value', '$S', propertyName)
                .build ()
            )

        if( apiOptions.beanValidation) {
            fieldBuilder.addAnnotations (beanValidationFactory.generateAnnotations (dataType))
        }

        fieldBuilder.build ()
    }
}
