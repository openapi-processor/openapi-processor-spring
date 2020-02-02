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

package com.github.hauner.openapi.spring.model

import com.github.hauner.openapi.spring.model.parameters.Parameter

/**
 * Endpoint properties.
 *
 * @author Martin Hauner
 */
class Endpoint {
    String path
    HttpMethod method

    List<Parameter> parameters = []
    List<RequestBody> requestBodies = []
    LinkedHashMap<String, List<Response>> responses = [:]

    void addResponses (String httpStatus, List<Response> statusResponses) {
        responses.put (httpStatus, statusResponses)
    }

    RequestBody getRequestBody () {
        requestBodies.first ()
    }

    @Deprecated
    Response getResponse () {
        responses.values ().first ().first ()
    }

    Set<String> getResponseImports () {
        responses
            .values ()
            .flatten ()
            .collect { it.imports }
            .flatten () as Set<String>
    }

    /**
     * checks if the endpoint contains multiple http status with responses, e.g. for status 200 and
     * default (or a specific error code).
     *
     * @return true if condition is met, else false
     */
    boolean hasMultiStatusResponses () {
        responses.size () > 1
    }

    boolean hasResponseContentTypes () {
        !responseContentTypes.empty
    }

    List<String> getResponseContentTypes () {
        def results = []
        responses.each {
            def contentType = it.value.first ().contentType
            if (contentType) {
                results.add (contentType)
            }
        }
        results
    }

}
