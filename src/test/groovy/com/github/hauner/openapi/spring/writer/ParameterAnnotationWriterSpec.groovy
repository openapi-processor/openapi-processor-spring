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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.core.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.core.model.datatypes.LongDataType
import com.github.hauner.openapi.core.model.datatypes.StringDataType
import com.github.hauner.openapi.core.model.parameters.CookieParameter
import com.github.hauner.openapi.core.model.parameters.HeaderParameter
import com.github.hauner.openapi.core.model.parameters.PathParameter
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.processor.SpringFrameworkAnnotations
import spock.lang.Specification
import spock.lang.Unroll

class ParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(annotations: new SpringFrameworkAnnotations())
    def target = new StringWriter()

    @Unroll
    void "writes simple (optional) query parameter with quoted string default value" () {
        def param = clazz.newInstance(
            name: 'foo',
            required: false,
            dataType: new StringDataType(constraints: new DataTypeConstraints (defaultValue: 'bar')))

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
        def param = clazz.newInstance (
            name: 'foo',
            required: false,
            dataType: new LongDataType (constraints: new DataTypeConstraints (defaultValue: 5)))

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

}
