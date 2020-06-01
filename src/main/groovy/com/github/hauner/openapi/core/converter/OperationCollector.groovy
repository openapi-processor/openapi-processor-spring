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

package com.github.hauner.openapi.core.converter

import com.github.hauner.openapi.core.model.HttpMethod
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem

/**
 * Collects a list of all used http methods of the given path (i.e. endpoint).
 *
 * @author Martin Hauner
 */
class OperationCollector {

    Map<HttpMethod, Operation> collect (PathItem item) {
        def ops = [:] as Map<HttpMethod, Operation>

        HttpMethod.values ().each {
            Operation op = item."${it.method}"
            if (op) {
                ops.put (it, op)
            }
        }

        ops
    }

}
