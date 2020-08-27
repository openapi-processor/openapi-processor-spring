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

import com.github.hauner.openapi.micronaut.model.parameters.QueryParameter
import com.github.hauner.openapi.micronaut.processor.MicronautFrameworkAnnotations
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.PathParameter
import spock.lang.Specification
import spock.lang.Unroll

class ParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(annotations: new MicronautFrameworkAnnotations ())
    def target = new StringWriter()

    @Unroll
    void "writes simple (optional) query parameter with quoted string default value" () {
        def param = clazz.newInstance('foo',
            new StringDataType(createConstraints ('bar'),false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == """${annotation}(value = "foo", defaultValue = "bar")"""

        where:
        clazz           | annotation
        QueryParameter  | "@QueryValue"
        PathParameter   | "@PathVariable"
        CookieParameter | "@CookieValue"
        HeaderParameter | "@Header"
    }

    void "writes simple (optional) query parameter with quoted number default value" () {
        def param = clazz.newInstance ('foo',
            new LongDataType (createConstraints (5), false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == """${annotation}(value = "foo", defaultValue = "5")"""

        where:
        clazz           | annotation
        QueryParameter  | "@QueryValue"
        PathParameter   | "@PathVariable"
        CookieParameter | "@CookieValue"
        HeaderParameter | "@Header"
    }

    void "writes request body parameter" () {
        def body = new RequestBody ('body', 'application/json',
            new ObjectDataType ('FooRequestBody', '', ['foo': new StringDataType ()],
                null, false), false, false)

        when:
        writer.write (target, body)

        then:
        target.toString () == "@Body"
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
