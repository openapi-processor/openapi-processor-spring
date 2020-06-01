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

/**
 * Mapping of http methods to Spring annotations.
 *
 * @author Martin Hauner
 */
package com.github.hauner.openapi.core.model

enum HttpMethod {
    GET ('get'),
    PUT ('put'),
    POST ('post'),
    DELETE ('delete'),
    OPTIONS ('options'),
    HEAD ('head'),
    PATCH ('patch'),
    TRACE ('trace')

    private String method

    HttpMethod(String method) {
        this.method = method
    }

    String getMethod() {
        method
    }

    HttpMethod from(String method) {
        values ().find {
            it.method == method
        }
    }

}
