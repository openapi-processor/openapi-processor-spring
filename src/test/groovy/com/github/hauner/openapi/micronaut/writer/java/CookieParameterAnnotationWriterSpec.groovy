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

import com.github.hauner.openapi.micronaut.processor.MicronautFrameworkAnnotations
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import spock.lang.Specification

class CookieParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(annotations: new MicronautFrameworkAnnotations ())
    def target = new StringWriter()

    void "write simple (required) cookie parameter" () {
        def param = new CookieParameter('foo', new StringDataType(), false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@CookieValue(value = "foo")'
    }

    void "write simple (optional, with default value) cookie parameter" () {
        def param = new CookieParameter('foo',
            new StringDataType(new DataTypeConstraints(defaultValue: 'bar'), false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@CookieValue(value = "foo", defaultValue = "bar")'
    }

}
