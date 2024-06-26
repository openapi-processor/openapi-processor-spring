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

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import spock.lang.Specification
import spock.lang.Unroll

class ParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(new SpringFrameworkAnnotations())
    def target = new StringWriter()

    @Unroll
    void "writes simple (optional) query parameter with quoted string default value" () {
        def param = clazz.newInstance('foo',
            new StringDataType(
                'string',
                createConstraints ('bar'), false, null),
            false, false, null)

        when:
        writer.write (target, param)

        then:
        target.toString () == """${annotation}(name = "foo", required = false, defaultValue = "bar")"""

        where:
        clazz           | annotation
        QueryParameter  | "@RequestParam"
        PathParameter   | "@PathVariable"
        CookieParameter | "@CookieValue"
        HeaderParameter | "@RequestHeader"
    }

    void "writes simple (optional) query parameter with quoted number default value" () {
        def param = clazz.newInstance ('foo',
            new LongDataType (
                'integer:int64',
                createConstraints (5), false, null),
            false, false, null)

        when:
        writer.write (target, param)

        then:
        target.toString () == """${annotation}(name = "foo", required = false, defaultValue = "5")"""

        where:
        clazz           | annotation
        QueryParameter  | "@RequestParam"
        PathParameter   | "@PathVariable"
        CookieParameter | "@CookieValue"
        HeaderParameter | "@RequestHeader"
    }

    void "writes required request body parameter" () {
        def body = new RequestBody (
            'body', 'application/json',
            new ObjectDataType (new DataTypeName('FooRequestBody', 'FooRequestBody'), '',
                ['foo': new StringDataType ()], null, false, null),
            true, false, null)

        when:
        writer.write (target, body)

        then:
        target.toString () == "@RequestBody"
    }

    void "writes optional request body parameter" () {
        def body = new RequestBody (
            'body', 'application/json',
            new ObjectDataType (new DataTypeName('FooRequestBody', 'FooRequestBody'), '',
                ['foo': new StringDataType ()], null, false, null),
            false, false, null)

        when:
        writer.write (target, body)

        then:
        target.toString () == "@RequestBody(required = false)"
    }

    DataTypeConstraints createConstraints(def defaultValue) {
        def c = new DataTypeConstraints()
        c.defaultValue = defaultValue
        c
    }

}
