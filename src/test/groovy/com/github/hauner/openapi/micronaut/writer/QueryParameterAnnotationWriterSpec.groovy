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

package com.github.hauner.openapi.micronaut.writer

import com.github.hauner.openapi.micronaut.model.parameters.QueryParameter
import com.github.hauner.openapi.micronaut.processor.MicronautFrameworkAnnotations
import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import spock.lang.Specification

class QueryParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(annotations: new MicronautFrameworkAnnotations())
    def target = new StringWriter()

    void "write simple (required, no default value) query parameter" () {
        def param = new QueryParameter(name: 'foo',
            required: true, // ignored
            dataType: new StringDataType())

        when:
        writer.write (target, param)

        then:
        target.toString () == '@QueryValue(value = "foo")'
    }

    void "write simple (optional, with default value) query parameter" () {
        def param = new QueryParameter(name: 'foo',
            required: true, // ignored
            dataType: new StringDataType(constraints: new DataTypeConstraints(defaultValue: 'bar')))

        when:
        writer.write (target, param)

        then:
        target.toString () == '@QueryValue(value = "foo", defaultValue = "bar")'
    }

}
