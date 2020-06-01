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

package com.github.hauner.openapi.core.parser.swagger

import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.parser.Operation as ParserOperation
import com.github.hauner.openapi.core.parser.Parameter as ParserParameter
import com.github.hauner.openapi.core.parser.RequestBody as ParserRequestBody
import com.github.hauner.openapi.core.parser.Response as ParserResponse
import io.swagger.v3.oas.models.Operation as SwaggerOperation
import io.swagger.v3.oas.models.parameters.Parameter as SwaggerParameter
import io.swagger.v3.oas.models.responses.ApiResponse as SwaggerResponse


/**
 * Swagger Operation abstraction.
 *
 * @author Martin Hauner
 */
class Operation implements ParserOperation {

    HttpMethod method
    private SwaggerOperation operation

    Operation (HttpMethod method, SwaggerOperation operation) {
        this.method = method
        this.operation = operation
    }

    @Override
    String getOperationId () {
        operation.operationId
    }

    @Override
    List<ParserParameter> getParameters () {
        def params = []
        operation.parameters.each { SwaggerParameter p ->
            params.add (new Parameter(p))
        }
        params
    }

    @Override
    ParserRequestBody getRequestBody () {
        if (!operation.requestBody) {
            return null
        }

        new RequestBody (operation.requestBody)
    }

    @Override
    Map<String, ParserResponse> getResponses () {
        def content = [:] as LinkedHashMap

        operation.responses.each { Map.Entry<String, SwaggerResponse> entry ->
            content.put (entry.key, new Response(entry.value))
        }

        content
    }

    @Override
    boolean hasTags () {
        operation.tags ? !operation.tags.empty : false
    }

    @Override
    String getFirstTag () {
        operation.tags.first ()
    }

}
