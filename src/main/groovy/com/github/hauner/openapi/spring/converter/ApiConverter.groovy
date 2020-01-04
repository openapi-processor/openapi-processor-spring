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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.converter.schema.ParameterSchemaInfo
import com.github.hauner.openapi.spring.converter.schema.RefResolver
import com.github.hauner.openapi.spring.converter.schema.ResponseSchemaInfo
import com.github.hauner.openapi.spring.converter.schema.SchemaInfo
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.RequestBody
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.parameters.CookieParameter
import com.github.hauner.openapi.spring.model.parameters.HeaderParameter
import com.github.hauner.openapi.spring.model.parameters.MultipartParameter
import com.github.hauner.openapi.spring.model.parameters.Parameter as ModelParameter
import com.github.hauner.openapi.spring.model.parameters.PathParameter
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.support.Identifier
import groovy.util.logging.Slf4j
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.parameters.Parameter
import io.swagger.v3.oas.models.responses.ApiResponse

/**
 * Converts the open api model to a new model that is better suited for generating source files
 * from the open api specification.
 *
 * @author Martin Hauner
 */
@Slf4j
class ApiConverter {
    public static final String MULTIPART = "multipart/form-data"

    private DataTypeConverter dataTypeConverter
    private ApiOptions options

    ApiConverter(ApiOptions options) {
        this.options = options

        if (!this.options) {
            this.options = new DefaultApiOptions()
        }

        dataTypeConverter = new DataTypeConverter(this.options)
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
        def resolver = new RefResolver (api.components)

        api.paths.each { Map.Entry<String, PathItem> pathEntry ->
            String path = pathEntry.key
            PathItem pathItem = pathEntry.value

            def operations = new OperationCollector ().collect (pathItem)
            operations.each { httpOperation ->
                def itf = target.getInterface (getInterfaceName (httpOperation))

                Endpoint ep = new Endpoint (path: path, method: httpOperation.httpMethod)

                try {
                    httpOperation.parameters.each { Parameter parameter ->
                        ep.parameters.add (createParameter(path, parameter, target, resolver))
                    }

                    if (httpOperation.requestBody != null) {
                        def required = httpOperation.requestBody.required != null ?: false
                        httpOperation.requestBody.content.each { Map.Entry<String, MediaType> requestBodyEntry ->
                            def contentType = requestBodyEntry.key
                            def requestBody = requestBodyEntry.value

                            def info = new SchemaInfo (path, requestBody.schema, getInlineTypeName (path))
                            info.resolver = resolver

                            if (contentType == MULTIPART) {
                                ep.parameters.addAll (createMultipartParameter (info, required))
                            } else {
                                ep.requestBodies.add (createRequestBody (contentType, info, required, target.models))
                            }
                        }
                    }

                    httpOperation.responses.each { Map.Entry<String, ApiResponse> responseEntry ->
                        def httpStatus = responseEntry.key
                        def httpResponse = responseEntry.value

                        if (!httpResponse.content) {
                            ep.responses.add (createEmptyResponse ())
                        } else {
                            List<Response> responses = createResponses (
                                path,
                                httpResponse,
                                getInlineResponseName (path, httpStatus),
                                target,
                            resolver)

                            ep.responses.addAll (responses)
                        }
                    }

                    itf.endpoints.add (ep)

                } catch (UnknownDataTypeException e) {
                    log.error ("failed to parse endpoint {} {} because of: '{}'", ep.path, ep.method, e.message)
                }
            }
        }
    }

    private Collection<ModelParameter> createMultipartParameter (SchemaInfo info, boolean required) {
        DataType dataType = dataTypeConverter.convert (info, new DataTypes())
        if (! (dataType instanceof ObjectDataType)) {
            throw new MultipartResponseBodyException(info.path)
        }

        dataType.getObjectProperties ().collect {
            new MultipartParameter (name: it.key, required: required, dataType: it.value)
        }
    }

    private RequestBody createRequestBody (String contentType, SchemaInfo info, boolean required, DataTypes dataTypes) {
        DataType dataType = dataTypeConverter.convert (info, dataTypes)

        new RequestBody(
            contentType: contentType,
            requestBodyType: dataType,
            required: required)
    }

    private ModelParameter createParameter (String path, Parameter parameter, Api target, resolver) {
        def info = new ParameterSchemaInfo (path, parameter.schema, parameter.name)
        info.resolver = resolver

        DataType dataType = dataTypeConverter.convert (info, target.models)

        switch (parameter.in) {
            case 'query':
                return new QueryParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            case 'path':
                return new PathParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            case 'header':
                return new HeaderParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            case 'cookie':
                return new CookieParameter (name: parameter.name, required: parameter.required, dataType: dataType)
            default:
                // should not reach this, the openapi parser ignores parameters with unknown type.
                throw new UnknownParameterTypeException(parameter.name, parameter.in)
        }
    }

    private String getInlineTypeName (String path) {
        Identifier.toClass (path) + 'RequestBody'
    }

    private String getInlineResponseName (String path, String httpStatus) {
        Identifier.toClass (path) + 'Response' + httpStatus
    }

    private Response createEmptyResponse () {
        new Response (responseType: dataTypeConverter.none ())
    }

    private List<Response> createResponses (String path, ApiResponse apiResponse, String inlineName, Api target, RefResolver resolver) {
        def responses = []

        apiResponse.content.each { Map.Entry<String, MediaType> contentEntry ->
            def contentType = contentEntry.key
            def mediaType = contentEntry.value
            def schema = mediaType.schema

            def info = new ResponseSchemaInfo (
                path,
                contentType,
                schema,
                inlineName)
            info.resolver = resolver

            DataType dataType = dataTypeConverter.convert (
                info,
                target.models)

            def response = new Response (
                contentType: contentType,
                responseType: dataType)

            responses.add (response)
        }

        responses
    }

    private void collectInterfaces (OpenAPI api, Api target) {
        target.interfaces = new InterfaceCollector (options)
            .collect (api.paths)
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
