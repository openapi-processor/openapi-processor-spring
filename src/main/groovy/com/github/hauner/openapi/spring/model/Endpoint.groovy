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
    String operationId

    List<Parameter> parameters = []
    List<RequestBody> requestBodies = []
    LinkedHashMap<String, List<Response>> responses = [:]
    
    // grouped responses
    List<EndpointResponse> endpointResponses = []

    void addResponses (String httpStatus, List<Response> statusResponses) {
        responses.put (httpStatus, statusResponses)
    }

    Endpoint initEndpointResponses () {
        endpointResponses = createEndpointResponses ()
        this
    }
    
    
    RequestBody getRequestBody () {
        requestBodies.first ()
    }

    /**
     * all possible responses for an openapi status ('200', '2xx',... or 'default'). If the given
     * status has no responses the result is an empty list.
     *
     * @param status the response status
     * @return the list of responses
     */
    List<Response> getResponses (String status) {
        if (!responses.containsKey (status)) {
            []
        }
        responses[status]
    }

    /**
     * test support
     *
     * @param status the response status
     * @return first response of status
     */
    Response getFirstResponse (String status) {
        if (!responses.containsKey (status)) {
            null
        }

        def resp = responses[status]
        if (resp.empty) {
            null
        }

        resp.first ()
    }

    /**
     * checks if the endpoint has multiple success responses with different content types.
     *
     * @return true if condition is met, otherwise false.
     */
    boolean hasMultipleEndpointResponses () {
        endpointResponses.size () > 1
    }

    /**
     * creates groups from the responses.
     *
     * if the endpoint does provide its result in multiple content types it will create one entry
     * for each response kind (main response). if error responses are defined they are added as
     * error responses.
     *
     * this is used to create one controller method for each (successful) response definition.
     *
     * @return list of method responses
     */
    private List<EndpointResponse> createEndpointResponses () {
        Set<Response> oks = successResponses
        Set<Response> errors = errorResponses
        oks.collect {
            new EndpointResponse(main: it, errors: errors)
        }
    }

    /**
     * finds the success responses
     */
    private Set<Response> getSuccessResponses () {
        Map<String, Response> result = [:]

        responses.findAll {
            it.key.startsWith ('2')
        }.each {
            it.value.each {
                result.put (it.contentType, it)
            }
        }

        result.values () as Set<Response>
    }

    /**
     * finds the error responses
     */
    private Set<Response> getErrorResponses () {
        responses.findAll {
            !it.key.startsWith ('2')
        }.collect {
            it.value.first ()
        } as Set<Response>
    }

}
