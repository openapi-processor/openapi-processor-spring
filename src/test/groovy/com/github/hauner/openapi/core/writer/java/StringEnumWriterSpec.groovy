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

package com.github.hauner.openapi.core.writer.java

import com.github.hauner.openapi.core.model.datatypes.StringEnumDataType
import com.github.hauner.openapi.spring.writer.java.HeaderWriter
import spock.lang.Specification

class StringEnumWriterSpec extends Specification {
    def headerWriter = Mock HeaderWriter

    def writer = new StringEnumWriter(headerWriter: headerWriter)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def dataType = new StringEnumDataType(type: 'Foo', values: [])

        when:
        writer.write (target, dataType)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(type: 'Foo', values: [], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
package $pkg;

""")
    }

    void "writes enum class"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(type: 'Foo', values: [], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
public enum Foo {
""")
        target.toString ().contains ("""\
}
""")
    }

    void "writes enum values"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(type: 'Foo', values: ['foo', '_foo-2', 'foo-foo'], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
public enum Foo {
    FOO("foo"),
    FOO_2("_foo-2"),
    FOO_FOO("foo-foo");

""")
    }

    void "writes value member"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(type: 'Foo', values: ['foo', '_foo-2', 'foo-foo'], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    private final String value;

""")
    }

    void "writes enum constructor"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['foo', '_foo-2', 'foo-foo'], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\

    private Foo(String value) {
        this.value = value;
    }

""")
    }

    void "writes @JsonValue method for serialization"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['foo', '_foo-2', 'foo-foo'], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\

    @JsonValue
    public String getValue() {
        return this.value;
    }

""")
    }

    void "writes @JsonCreator method for de-serialization"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType (type: 'Foo', values: ['foo', '_foo-2', 'foo-foo'], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\

    @JsonCreator
    public static Foo fromValue(String value) {
        for (Foo val : Foo.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }

""")
    }

    void "writes jackson imports" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new StringEnumDataType(type: 'Foo', values: [], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

""")
    }

}



