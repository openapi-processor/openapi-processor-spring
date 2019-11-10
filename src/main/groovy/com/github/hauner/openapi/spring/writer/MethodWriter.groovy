/*
 * Copyright 2019 the original authors
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
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.support.Identifier

/**
 * Writer for Java interface methods, i.e. endpoints.
 *
 * @author Martin Hauner
 */
class MethodWriter {

    void write (Writer target, Endpoint endpoint) {
        target.write ("""\
    ${createMappingAnnotation (endpoint)}
    ResponseEntity<${endpoint.response.responseType.name}> ${createMethodName (endpoint)}(${createParameter(endpoint)});
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

    private String createParameterAnnotation (QueryParameter parameter) {
        String param = "${parameter.annotation}"

        if (! parameter.withParameters ()) {
            return param;
        }

        param += '('
        param += 'name = ' + quote (parameter.name)

        // required is default, so add required only if the parameter is not required
        if (!parameter.required) {
            param += ", "
            param += 'required = false'
        }

        param += ')'
        param
    }

    private String createMethodName (Endpoint endpoint) {
        def tokens = endpoint.path.tokenize ('/')
        tokens = tokens.collect { Identifier.fromJson (it).capitalize () }
        def name = tokens.join ('')
        "${endpoint.method.method}${name}"
    }

    private String createParameter (Endpoint endpoint) {
        def ps = endpoint.parameters.collect {

            if (it.withAnnotation ()) {
                "${createParameterAnnotation (it)} ${it.dataType.name} ${Identifier.fromJson (it.name)}"
            } else {
                "${it.dataType.name} ${Identifier.fromJson (it.name)}"
            }

        }

        ps.join (', ')
    }

    private String quote (String content) {
        '"' + content + '"'
    }

}
