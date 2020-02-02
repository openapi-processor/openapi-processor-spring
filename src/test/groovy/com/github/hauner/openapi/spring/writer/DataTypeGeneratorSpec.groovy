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

import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import spock.lang.Specification

import javax.lang.model.element.Modifier
import java.util.stream.Collectors

class DataTypeGeneratorSpec extends Specification {
    def options = new ApiOptions()
    def writer = new DataTypeGenerator(apiOptions: options)

    void "generate class" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new ObjectDataType (name: 'Book', properties: [:] as Map, packageName: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec != null
        typeSpec.name == 'Book'
        typeSpec.kind == TypeSpec.Kind.CLASS
        typeSpec.modifiers.contains (Modifier.PUBLIC)
        typeSpec.fieldSpecs.isEmpty ()
        typeSpec.methodSpecs.isEmpty ()
    }

    void "generate properties with @JsonProperty annotation" () {
        def pkg = 'com.github.hauner.openapi'

        def dataType = new ObjectDataType (name: 'Book', properties: [
            'isbn': new StringDataType (),
            'title': new StringDataType ()
        ], packageName: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.fieldSpecs.size () == 2
        def isbnField = typeSpec.fieldSpecs.find { it.name == 'isbn' }
        isbnField.modifiers.contains (Modifier.PRIVATE)
        isbnField.type.canonicalName == 'java.lang.String'
        isbnField.annotations.size () == 1
        isbnField.annotations[0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonProperty'
        isbnField.annotations[0].members.size () == 1
        isbnField.annotations[0].members.get ('value')[0] as String == '"isbn"'

        def aTitleField = typeSpec.fieldSpecs.find { it.name == 'title' }
        aTitleField.modifiers.contains (Modifier.PRIVATE)
        aTitleField.type.canonicalName () == 'java.lang.String'
        aTitleField.annotations.size () == 1
        aTitleField.annotations[0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonProperty'
        aTitleField.annotations[0].members.size () == 1
        aTitleField.annotations[0].members.get ('value')[0] as String == '"title"'
    }

    void "generate valid java properties with @JsonProperty annotation" () {
        def pkg = 'com.github.hauner.openapi'

        def dataType = new ObjectDataType (name: 'Book', properties: [
            'a-isbn': new StringDataType (),
            'a-title': new StringDataType ()
        ], packageName: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.fieldSpecs.size () == 2
        def isbnField = typeSpec.fieldSpecs.find { it.name == 'aIsbn' }
        isbnField.modifiers.contains (Modifier.PRIVATE)
        isbnField.type.canonicalName == 'java.lang.String'
        isbnField.annotations.size () == 1
        isbnField.annotations[0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonProperty'
        isbnField.annotations[0].members.size () == 1
        isbnField.annotations[0].members.get ('value')[0] as String == '"a-isbn"'

        def aTitleField = typeSpec.fieldSpecs.find { it.name == 'aTitle' }
        aTitleField.modifiers.contains (Modifier.PRIVATE)
        aTitleField.type.canonicalName () == 'java.lang.String'
        aTitleField.annotations.size () == 1
        aTitleField.annotations[0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonProperty'
        aTitleField.annotations[0].members.size () == 1
        aTitleField.annotations[0].members.get ('value')[0] as String == '"a-title"'
    }

    void "generate property getters & setters" () {
        def pkg = 'com.github.hauner.openapi'

        def dataType = new ObjectDataType (name: 'Book', properties: [
            'a-isbn': new StringDataType (),
            'a-title': new StringDataType ()
        ], packageName: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.methodSpecs.size () == 4

        def isbnGetter = typeSpec.methodSpecs.find { it.name == 'getAIsbn' }
        isbnGetter.modifiers.contains (Modifier.PUBLIC)
        isbnGetter.returnType.canonicalName == 'java.lang.String'
        isbnGetter.parameters.isEmpty ()
        def isbnGetterLines = isbnGetter.code.toString ().lines ().collect (Collectors.toList ())
        isbnGetterLines.size () == 1
        isbnGetterLines[0] == 'return aIsbn;'

        def isbnSetter = typeSpec.methodSpecs.find { it.name == 'setAIsbn' }
        isbnSetter.modifiers.contains (Modifier.PUBLIC)
        isbnSetter.returnType == TypeName.VOID
        isbnSetter.parameters.size () == 1
        isbnSetter.parameters[0].name == 'aIsbn'
        isbnSetter.parameters[0].type.canonicalName == 'java.lang.String'
        def isbnSetterLines = isbnSetter.code.toString ().lines ().collect (Collectors.toList ())
        isbnSetterLines.size () == 1
        isbnSetterLines[0] == 'this.aIsbn = aIsbn;'

        def titleGetter = typeSpec.methodSpecs.find { it.name == 'getATitle' }
        titleGetter.modifiers.contains (Modifier.PUBLIC)
        titleGetter.returnType.canonicalName == 'java.lang.String'
        def titleGetterLines = titleGetter.code.toString ().lines ().collect (Collectors.toList ())
        titleGetterLines.size () == 1
        titleGetterLines[0] == 'return aTitle;'

        def titleSetter = typeSpec.methodSpecs.find { it.name == 'setATitle' }
        titleSetter.modifiers.contains (Modifier.PUBLIC)
        titleSetter.returnType == TypeName.VOID
        titleSetter.parameters.size () == 1
        titleSetter.parameters[0].name == 'aTitle'
        titleSetter.parameters[0].type.canonicalName == 'java.lang.String'
        def titleSetterLines = titleSetter.code.toString ().lines ().collect (Collectors.toList ())
        titleSetterLines.size () == 1
        titleSetterLines[0] == 'this.aTitle = aTitle;'
    }

    void "generate imports of nested types" () {
        def pkg = 'external'

        def dataType = new ObjectDataType (name: 'Book', properties: [
            'isbn': new ObjectDataType (name: 'Isbn', properties: [:] as LinkedHashMap, packageName: pkg)
        ])

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.fieldSpecs.size () == 1
        def isbnField = typeSpec.fieldSpecs.find { it.name == 'isbn' }
        isbnField.modifiers.contains (Modifier.PRIVATE)
        isbnField.type.canonicalName == 'external.Isbn'
        isbnField.annotations.size () == 1
        isbnField.annotations[0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonProperty'
        isbnField.annotations[0].members.size () == 1
        isbnField.annotations[0].members.get ('value')[0] as String == '"isbn"'
    }

    void "generate import of generic list type" () {
        def dataType = new ObjectDataType (name: 'Book', properties: [
            'authors': new ListDataType (new StringDataType (), null)
        ])

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.fieldSpecs.size () == 1
        def authorField = typeSpec.fieldSpecs.find { it.name == 'authors' }
        authorField.modifiers.contains (Modifier.PRIVATE)
        authorField.type.rawType.canonicalName == 'java.util.List'
        authorField.type.typeArguments.size() == 1
        authorField.type.typeArguments[0].canonicalName == 'java.lang.String'
        authorField.annotations.size () == 1
        authorField.annotations[0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonProperty'
        authorField.annotations[0].members.size () == 1
        authorField.annotations[0].members.get ('value')[0] as String == '"authors"'
    }
}
