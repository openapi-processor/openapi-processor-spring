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

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.EndpointResponse
import io.openapiprocessor.core.support.capitalizeFirstChar
import io.openapiprocessor.core.writer.java.MappingAnnotationWriter as CoreMappingAnnotationWriter
import java.io.Writer

/**
 * spring mapping annotation writer
 *
 * @author Martin Hauner
 */
class MappingAnnotationWriter: CoreMappingAnnotationWriter {

    override fun write(target: Writer, endpoint: Endpoint, endpointResponse: EndpointResponse) {
        target.write(createAnnotation(endpoint, endpointResponse))
    }

    private fun createAnnotation(endpoint: Endpoint, endpointResponse: EndpointResponse): String {
        var mapping = getMappingAnnotation(endpoint)
        mapping += "("
        mapping += "path = " + quote(endpoint.path)

        val consumes = endpoint.getConsumesContentTypes()
        if (consumes.isNotEmpty()) {
            mapping += ", "
            mapping += "consumes = {"
            mapping +=  consumes.map {
                quote(it)
            }.joinToString(", ")
            mapping += '}'
        }

        val contentTypes = endpointResponse.contentTypes
        if (contentTypes.isNotEmpty()) {
            mapping += ", "
            mapping += "produces = {"

            mapping += contentTypes.map {
                quote (it)
            }.joinToString (", ")

            mapping += "}"
        }

        mapping += ")"
        return mapping
    }

    private fun getMappingAnnotation(endpoint: Endpoint): String {
        return "@${endpoint.method.method.capitalizeFirstChar ()}Mapping"
    }

    private fun quote(content: String): String {
        return '"' + content + '"'
    }

}
