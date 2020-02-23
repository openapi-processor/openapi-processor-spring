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

package com.github.hauner.openapi.spring.parser.swagger

import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.spring.parser.Operation as ParserOperation
import com.github.hauner.openapi.spring.parser.Parameter as ParserParameter
import com.github.hauner.openapi.spring.parser.RequestBody as ParserRequestBody
import io.swagger.v3.oas.models.Operation as SwaggerOperation
import io.swagger.v3.oas.models.parameters.Parameter as SwaggerParameter


/**
 * Swagger Operation abstraction.
 *
 * @author Martin Hauner
 */
class Operation implements ParserOperation {

    HttpMethod method
    SwaggerOperation operation

    Operation (HttpMethod method, SwaggerOperation operation) {
        this.method = method
        this.operation = operation
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
        new RequestBody (operation.requestBody)
    }

    @Override
    boolean hasTags () {
        operation.tags ? !operation.tags.empty : false
    }

    @Override
    String getFirstTag () {
        operation.tags.first ()
    }

    @Deprecated
    SwaggerOperation getOperation () {
        operation
    }

}
