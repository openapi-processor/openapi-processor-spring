/*
 * Copyright 2019 https://github.com/hauner/openapi-spring-generator
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

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem

/**
 * collects a list of all used http methods of the given path (i.e. endpoint)
 */
class OperationCollector {
    static def methods = ['get', 'put', 'post', 'delete', 'options', 'head', 'patch', 'trace']

    List<Operation> collect (PathItem item) {
        def ops = []

        methods.each { m ->
            if (item."$m") {
                def op = item."$m" as HttpMethod // add trait
                op.httpMethod = "$m"
                ops << op
            }
        }

        ops
    }
}
