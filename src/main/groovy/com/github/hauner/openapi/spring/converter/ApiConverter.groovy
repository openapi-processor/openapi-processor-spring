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
import io.swagger.v3.oas.models.media.Schema as OaSchema
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

        collectModels (api, target)
        collectInterfaces (api, target)
        addEndpointsToInterfaces (api, target)

        target
    }

    private Map<String, PathItem> addEndpointsToInterfaces (OpenAPI api, Api target) {
        api.paths.each { Map.Entry<String, PathItem> pathEntry ->
            PathItem pathItem = pathEntry.value

            def operations = new OperationCollector ().collect (pathItem)
            operations.each { httpOperation ->
                def itf = target.getInterface (getInterfaceName (httpOperation))

                Endpoint ep = new Endpoint (path: pathEntry.key, method: httpOperation.httpMethod)

                httpOperation.responses.each { Map.Entry<String, ApiResponse> responseEntry ->
                    def httpStatus = responseEntry.key
                    def httpResponse = responseEntry.value

                    if (!httpResponse.content) {
                        ep.responses.add (createEmptyResponse ())
                    } else {
                        ep.responses.addAll (createResponses (httpResponse, target))
                    }
                }

                itf.endpoints.add (ep)
            }
        }
    }

    private Response createEmptyResponse () {
        def schema = new Schema (type: 'none')

        def response = new Response (
            responseType: schema)
        response
    }

    private List<Response> createResponses (ApiResponse apiResponse, Api target) {
        def responses = []

        apiResponse.content.each { Map.Entry<String, MediaType> contentEntry ->
            def contentType = contentEntry.key
            def mediaType = contentEntry.value

            Schema schema = getSchema (mediaType.schema, target)

            def response = new Response (
                contentType: contentType,
                responseType: schema)

            responses.add (response)
        }

        responses
    }

    private Schema getSchema (OaSchema schema, Api target) {
        if (isRefObject (schema)) {
            getModel(schema.$ref, target)
        } else if (isInlineObject (schema)) {
            new Schema (type: 'map')
        } else {
            new Schema (type: schema.type, format: schema.format)
        }
    }

    private Schema getModel (String ref, Api target) {
        def idx = ref.lastIndexOf ('/')
        def path = ref.substring (0, idx + 1 )
        def name = ref.substring (idx + 1 )

        if (path != '#/components/schemas/') {
            return null
        }

        target.getModel (name)
    }

    private boolean isRefObject (OaSchema schema) {
        schema.$ref != null
    }

    private boolean isInlineObject (OaSchema schema) {
        schema.type == 'object'
    }

    private void collectInterfaces (OpenAPI api, Api target) {
        target.interfaces = new InterfaceCollector (options)
            .collect (api.paths)
    }

    private void collectModels (OpenAPI api, Api target) {
        if (!api.components || !api.components.schemas) {
            return
        }

        target.models = new SchemaCollector()
            .collect (api.components.schemas)
    }

    private String getInterfaceName(def operation) {
        if (!hasTags (operation)) {
            return InterfaceCollector.INTERFACE_DEFAULT_NAME
        }

        operation.tags.first ()
    }

    private boolean hasTags (op) {
        op.tags && !op.tags.empty
    }
}
