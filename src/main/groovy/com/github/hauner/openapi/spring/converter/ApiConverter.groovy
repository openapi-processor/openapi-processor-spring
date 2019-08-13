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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.generatr.DefaultApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.Schema
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 */
class ApiConverter {

    private ApiOptions options

    ApiConverter(ApiOptions options) {
        this.options = options

        if (!this.options) {
            this.options = new DefaultApiOptions()
        }
    }

    /**
     * converts the openapi model to the source generation model
     *
     * @param api the open api model
     * @return source generation model
     */
    Api convert (OpenAPI api) {
        def target = new Api ()

        collectInterfaces (api, target)
        addEndpointsToInterfaces (api, target)

        target
    }

    private Map<String, PathItem> addEndpointsToInterfaces (OpenAPI api, Api target) {
        api.paths.each { Map.Entry<String, PathItem> pathEntry ->
            PathItem pathItem = pathEntry.value

            def operations = new OperationCollector ().collect (pathItem)
            operations.each { httpMethod ->
                def itf = target.getInterface (httpMethod.tags.first ())

                Endpoint ep = new Endpoint (path: pathEntry.key, method: httpMethod.httpMethod)

                httpMethod.responses.each { Map.Entry<String, ApiResponse> responseEntry ->
                    def httpStatus = responseEntry.key
                    def httpResponse = responseEntry.value

                    if (!httpResponse.content) {
                        ep.responses.push (createEmptyResponse ())
                    } else {
                        ep.responses.addAll (createResponses (httpResponse))
                    }
                }

                itf.endpoints.push (ep)
            }
        }
    }

    private Response createEmptyResponse () {
        def schema = new Schema (type: 'none')

        def response = new Response (
            responseType: schema)
        response
    }

    private List<Response> createResponses (ApiResponse apiResponse) {
        def responses = []

        apiResponse.content.each { Map.Entry<String, MediaType> contentEntry ->
            def contentType = contentEntry.key
            def mediaType = contentEntry.value

            def schema = new Schema(type: mediaType.schema.type)

            def response = new Response (
                contentType: contentType,
                responseType: schema)

            responses.push (response)
        }

        responses
    }

    private void collectInterfaces (OpenAPI api, Api target) {
        target.interfaces = new InterfaceCollector (options)
            .collect (api.paths)
    }
}
