/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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

import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Schema

class MethodWriter {

    static final KNOWN_DATA_TYPES = [
        none: [
            default: 'void'
        ],
        string: [
            default: 'String'
        ],
        integer: [
            default: 'Integer',
            int32: 'Integer',
            int64: 'Long'
        ],
        number: [
            default: 'Float',
            float: 'Float',
            double: 'Double'
        ],
        boolean: [
            default: 'Boolean'
        ]
    ]

    void write (Writer target, Endpoint endpoint) {
        target.write ("""\
    ${createMappingAnnotation (endpoint)}
    ResponseEntity<${getType (endpoint.response.responseType)}> ${createMethodName (endpoint)}();
""")
    }

    private String createMappingAnnotation (Endpoint endpoint) {
        String mapping = "${endpoint.method.mappingAnnotation}"
        mapping += "("
        mapping += 'path = ' + quote(endpoint.path)

        if (!endpoint.response.empty) {
            mapping += ", "
            mapping += 'produces = {' + quote(endpoint.response.contentType) + '}'
        }

        mapping += ")"
        mapping
    }

    private String getType (Schema schema) {
        def type = KNOWN_DATA_TYPES.get (schema.type)
        if (schema.format) {
            type."${schema.format}"
        } else {
            type.default
        }
    }

    private String createMethodName (Endpoint endpoint) {
        def tokens = endpoint.path.tokenize ('/')
        tokens = tokens.collect {it.capitalize ()}
        def name = tokens.join ('')
        "${endpoint.method.method}${name}"
    }

    private String quote (String content) {
        '"' + content + '"'
    }

}
