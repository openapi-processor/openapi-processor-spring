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

package com.github.hauner.openapi.spring.model

import static com.github.hauner.openapi.extension.ToUpperCaseFirstExtension.*

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

    String getClassName () {
        toUpperCaseFirst (method) + "Mapping"
    }

    String getClassNameWithPackage () {
        "org.springframework.web.bind.annotation.${className}"
    }

    String getMappingAnnotation () {
        "@${className}"
    }
}
