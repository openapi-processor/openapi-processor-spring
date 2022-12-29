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

import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import spock.lang.Specification

class CookieParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(new SpringFrameworkAnnotations())
    def target = new StringWriter()

    void "write simple (required) cookie parameter" () {
        def param = new CookieParameter(
            'foo', new StringDataType(), true, false, null)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@CookieValue(name = "foo")'
    }

    void "write simple (optional) cookie parameter" () {
        def param = new CookieParameter(
            'foo', new StringDataType(), false, false, null)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@CookieValue(name = "foo", required = false)'
    }

    void "write simple (optional with default) cookie parameter" () {
        def param = new CookieParameter('foo',
            new StringDataType('string',
                createConstraints ('bar'),
                false, null),
            false, false, null)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@CookieValue(name = "foo", required = false, defaultValue = "bar")'
    }

    DataTypeConstraints createConstraints(def defaultValue) {
        def c = new DataTypeConstraints()
        c.defaultValue = defaultValue
        c
    }

}
