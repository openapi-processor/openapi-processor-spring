/*
 * Copyright 2020 the original authors
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

package com.github.hauner.openapi.micronaut.writer.java

import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import com.github.hauner.openapi.micronaut.model.parameters.QueryParameter
import com.github.hauner.openapi.micronaut.processor.MicronautFrameworkAnnotations
import spock.lang.Specification

class QueryParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(annotations: new MicronautFrameworkAnnotations())
    def target = new StringWriter()

    void "write simple (required, no default value) query parameter" () {
        def param = new QueryParameter('foo',
            new StringDataType(),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@QueryValue(value = "foo")'
    }

    void "write simple (optional, with default value) query parameter" () {
        def param = new QueryParameter('foo',
            new StringDataType(createConstraints ('bar'), false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@QueryValue(value = "foo", defaultValue = "bar")'
    }

    void "writes simple (optional) query parameter with quoted string default value" () {
        def param = new QueryParameter('foo',
            new StringDataType(createConstraints ('bar'), false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@QueryValue(value = "foo", defaultValue = "bar")'
    }

    void "writes simple (optional) query parameter with quoted number default value" () {
        def param = new QueryParameter('foo',
            new LongDataType (createConstraints (5), false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@QueryValue(value = "foo", defaultValue = "5")'
    }

    void "writes object query parameter without annotation" () {
        def param = new QueryParameter(
            'foo',
            new ObjectDataType (
                'Foo', '', [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ], null, false
            ), false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == ""
    }

    DataTypeConstraints createConstraints(def defaultValue) {
        new DataTypeConstraints(defaultValue,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null)
    }

}
