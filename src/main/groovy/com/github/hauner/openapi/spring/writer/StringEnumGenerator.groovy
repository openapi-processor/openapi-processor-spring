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

import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType
import com.github.hauner.openapi.support.Identifier
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.FieldSpec
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import com.squareup.javapoet.TypeSpec

import javax.lang.model.element.Modifier

/**
 * Writer for String enum.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class StringEnumGenerator {

    TypeSpec generateTypeSpec (StringEnumDataType dataType) {

        def typeName = dataType.type

        def typeBuilder = TypeSpec.enumBuilder (typeName)
            .addModifiers (Modifier.PUBLIC)
            .addField (generateValueFiledSpec ())
            .addMethod (generateConstructorMethodSpec ())
            .addMethod (generateGetValueMethodSpec ())
            .addMethod (generateFromValueMethodSpec (dataType, typeName))

        dataType.values.each {
            generateEnumConstraint (typeBuilder, it)
        }

        typeBuilder.build ()
    }

    private TypeSpec.Builder generateEnumConstraint (TypeSpec.Builder typeBuilder, String it) {
        typeBuilder.addEnumConstant (
            Identifier.toEnum (it),
            TypeSpec.anonymousClassBuilder ('$S', it).build ())
    }

    private FieldSpec generateValueFiledSpec () {
        FieldSpec.builder (String.class, 'value', Modifier.PRIVATE, Modifier.FINAL)
            .build ()
    }

    private MethodSpec generateConstructorMethodSpec () {
        MethodSpec.constructorBuilder ()
            .addModifiers (Modifier.PRIVATE)
            .addParameter (String.class, 'value')
            .addStatement ('this.value = value')
            .build ()
    }

    private MethodSpec generateGetValueMethodSpec () {
        MethodSpec.methodBuilder ('getValue')
            .addModifiers (Modifier.PUBLIC)
            .addAnnotation (ClassName.get ('com.fasterxml.jackson.annotation', 'JsonValue'))
            .returns (String.class)
            .addStatement ('return this.value')
            .build ()
    }

    private MethodSpec generateFromValueMethodSpec (StringEnumDataType dataType, String typeName) {
        MethodSpec.methodBuilder ('fromValue')
            .addModifiers (Modifier.PUBLIC, Modifier.STATIC)
            .addAnnotation (ClassName.get ('com.fasterxml.jackson.annotation', 'JsonCreator'))
            .addParameter (ParameterSpec.builder (String.class, 'value').build ())
            .returns (ClassName.get (dataType.packageName, dataType.type))
                .beginControlFlow ('for ($1N val : $1N.values())', typeName)
                    .beginControlFlow ('if (val.value.equals(value))')
                        .addStatement ('return val')
                    .endControlFlow ()
                .endControlFlow ()
                .addStatement ('throw new $T(value)', IllegalArgumentException.class)
            .build ()
    }
}
