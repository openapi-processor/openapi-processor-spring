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
import io.openapiprocessor.core.model.parameters.PathParameter
import spock.lang.Specification

class PathParameterAnnotationWriterSpec extends Specification {
    def writer = new ParameterAnnotationWriter(annotations: new MicronautFrameworkAnnotations())
    def target = new StringWriter()

    void "write simple (required, no default value) path parameter" () {
        def param = new PathParameter('foo', new StringDataType(), false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@PathVariable(value = "foo")'
    }

    void "write simple (optional, with default value) path parameter" () {
        def param = new PathParameter('foo',
            new StringDataType(createConstraints ('bar'), false),
            false, false)

        when:
        writer.write (target, param)

        then:
        target.toString () == '@PathVariable(value = "foo", defaultValue = "bar")'
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
