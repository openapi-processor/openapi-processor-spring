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

package com.github.hauner.openapi.core.parser.openapi4j

import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.parser.Operation as ParserOperation
import com.github.hauner.openapi.core.parser.Parameter as ParserParameter
import com.github.hauner.openapi.core.parser.RequestBody as ParserRequestBody
import com.github.hauner.openapi.core.parser.Response as ParserResponse
import org.openapi4j.parser.model.v3.Operation as O4jOperation
import org.openapi4j.parser.model.v3.Parameter as O4jParameter
import org.openapi4j.parser.model.v3.Response as O4jResponse

/**
 * openapi4j Operation abstraction.
 *
 * @author Martin Hauner
 */
class Operation implements ParserOperation {

    HttpMethod method
    private O4jOperation operation

    Operation (HttpMethod method, O4jOperation operation) {
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
        operation.parameters.each { O4jParameter p ->
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

        operation.responses.each { Map.Entry<String, O4jResponse> entry ->
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
