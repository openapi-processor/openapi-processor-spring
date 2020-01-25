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
import com.squareup.javapoet.TypeSpec
import spock.lang.Specification

import javax.lang.model.element.Modifier
import java.util.stream.Collectors

class StringEnumGeneratorSpec extends Specification {
    def writer = new StringEnumGenerator ()

    void "generate enum class" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['test'], pkg: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec != null
        typeSpec.name == 'Foo'
        typeSpec.kind == TypeSpec.Kind.ENUM
        typeSpec.modifiers.contains (Modifier.PUBLIC)
    }

    void "generate enum value Filed" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['test'], pkg: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.fieldSpecs.size () == 1
        typeSpec.fieldSpecs [0].name == 'value'
        typeSpec.fieldSpecs [0].type.canonicalName () == 'java.lang.String'
    }

    void "generate enum constructor" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['test'], pkg: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        // Constructor Definition
        def constructors = typeSpec.methodSpecs.findAll { it.isConstructor () }
        constructors.size () == 1
        constructors [0].modifiers.contains (Modifier.PRIVATE)
        constructors [0].parameters.size () == 1
        constructors [0].parameters [0].modifiers.isEmpty ()
        constructors [0].parameters [0].name == 'value'
        constructors [0].parameters [0].type.canonicalName () == 'java.lang.String'

        and:
        // Constructor Code
        def lines = constructors [0].code.toString ().lines ().collect (Collectors.toList ())
        lines.size () == 1
        lines [0].trim () == 'this.value = value;'
    }

    void "generate enum getValue" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['test'], pkg: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        // getValue Definition
        def methods = typeSpec.methodSpecs.findAll { it.name == "getValue" }
        methods.size () == 1
        methods [0].modifiers.contains (Modifier.PUBLIC)
        methods [0].returnType.canonicalName == 'java.lang.String'
        methods [0].parameters.isEmpty ()
        methods [0].annotations.size () == 1
        methods [0].annotations [0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonValue'
        methods [0].annotations [0].members.isEmpty ()

        and:
        // getValue Code
        def lines = methods [0].code.toString ().lines ().collect (Collectors.toList ())
        lines.size () == 1
        lines[0] == 'return this.value;'
    }

    void "generate enum fromValue" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['test'], pkg: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        // fromValue Definition
        def methods = typeSpec.methodSpecs.findAll { it.name == "fromValue" }
        methods.size () == 1
        methods [0].modifiers.contains (Modifier.PUBLIC)
        methods [0].modifiers.contains (Modifier.STATIC)
        methods [0].returnType.canonicalName == "${pkg}.${typeSpec.name}"
        methods [0].parameters.size () == 1
        methods [0].parameters [0].name == 'value'
        methods [0].parameters [0].type.canonicalName == 'java.lang.String'
        methods [0].annotations.size () == 1
        methods [0].annotations [0].type.canonicalName == 'com.fasterxml.jackson.annotation.JsonCreator'
        methods [0].annotations [0].members.isEmpty ()

        and:
        // fromValue Code
        def lines = methods [0].code.toString ().lines ().collect (Collectors.toList ())
        lines.size () == 6
        lines [0].trim () == ("for (${typeSpec.name} val : ${typeSpec.name}.values()) {")
        lines [1].trim () == ('if (val.value.equals(value)) {')
        lines [2].trim () == ('return val;')
        lines [3].trim () == ('}')
        lines [4].trim () == ('}')
        lines [5].trim () == ('throw new java.lang.IllegalArgumentException(value);')
    }

    void "generate enum values" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['foo', '_foo-2', 'foo-foo'], pkg: pkg)

        when:
        def typeSpec = writer.generateTypeSpec (dataType)

        then:
        typeSpec.enumConstants.get ("FOO").anonymousTypeArguments.toString () == '"foo"'
        typeSpec.enumConstants.get ("FOO_2").anonymousTypeArguments.toString () == '"_foo-2"'
        typeSpec.enumConstants.get ("FOO_FOO").anonymousTypeArguments.toString () == '"foo-foo"'
    }
}



