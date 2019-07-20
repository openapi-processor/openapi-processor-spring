/*
 * Copyright 2019 https://github.com/hauner/openapi-spring-generator
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
    static final MAPPING_ANNOTATION = [
        get: '@GetMapping'
    ]

    static final KNOWN_TYPES = [
        string: 'String'
    ]

    void write(Writer target, Endpoint endpoint) {
        target.write ("""\
    ${getMapping (endpoint.method)}(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<${getType (endpoint.response.responseType)}> ${getMethodName(endpoint)}();
""")
    }

    private String getMapping(String method) {
        MAPPING_ANNOTATION.get (method)
    }

    private String getType(Schema schema) {
        KNOWN_TYPES.get (schema.type)
    }

    private String getMethodName(Endpoint endpoint) {
        def tokens = endpoint.path.tokenize ('/')
        tokens = tokens.collect {it.toUpperCaseFirst ()}
        def name = tokens.join ('')
        "${endpoint.method}${name}"
    }

}
