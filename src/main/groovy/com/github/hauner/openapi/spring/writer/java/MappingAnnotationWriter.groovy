/*
 * Copyright 2019-2020 the original authors
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

package com.github.hauner.openapi.spring.writer.java

import com.github.hauner.openapi.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import com.github.hauner.openapi.core.model.Endpoint
import com.github.hauner.openapi.core.model.EndpointResponse

/**
 * spring mapping annotation writer
 *
 * @author Martin Hauner
 */
class MappingAnnotationWriter implements CoreMappingAnnotationWriter {

    @Override
    void write (Writer target, Endpoint endpoint, EndpointResponse endpointResponse) {
        target.write (createAnnotation (endpoint, endpointResponse))
    }

    private String createAnnotation (Endpoint endpoint, EndpointResponse endpointResponse) {
        String mapping = "${getMappingAnnotation (endpoint)}"
        mapping += "("
        mapping += 'path = ' + quote(endpoint.path)

        if (!endpoint.requestBodies.empty) {
            mapping += ", "
            mapping += 'consumes = {' + quote(endpoint.requestBody.contentType) + '}'
        }

        def contentTypes = endpointResponse.contentTypes
        if (!contentTypes.empty) {
            mapping += ", "
            mapping += 'produces = {'

            mapping += contentTypes.collect {
                quote (it)
            }.join (', ')

            mapping += '}'
        }

        mapping += ")"
        mapping
    }

    private String getMappingAnnotation (Endpoint endpoint) {
        "@${endpoint.method.method.capitalize ()}Mapping"
    }

    private String quote (String content) {
        '"' + content + '"'
    }

}
