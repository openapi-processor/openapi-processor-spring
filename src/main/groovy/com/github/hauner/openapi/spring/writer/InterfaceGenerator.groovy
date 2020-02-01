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
import com.github.hauner.openapi.spring.model.Interface
import com.squareup.javapoet.TypeSpec

import javax.lang.model.element.Modifier

/**
 * Writer for Java interfaces.
 *
 * @author Martin Hauner
 * @authro Bastian Wilhelm
 */
class InterfaceGenerator {
    ApiOptions apiOptions
    MethodGenerator methodWriter
    BeanValidationGenerator beanValidationFactory

    TypeSpec generateTypeSpec (Interface anInterface) {
        def typeSpecBuilder = TypeSpec.interfaceBuilder (anInterface.interfaceName)
            .addModifiers (Modifier.PUBLIC)

        anInterface.endpoints.each {
            typeSpecBuilder.addMethod (methodWriter.generateMethodSpec(it))
        }

        typeSpecBuilder.build ()
    }
}
