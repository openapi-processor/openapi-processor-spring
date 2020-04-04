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

import com.github.hauner.openapi.spring.model.datatypes.ResultDataType

/**
 * The responses that can be returned by an endpoint method for one (successful) response.
 *
 * @author Martin Hauner
 */
class EndpointResponse {

    /**
     * success response
     */
    Response main

    /**
     * additional (error) responses
     */
    Set<Response> errors


    String getContentType () {
        main.contentType
    }

    /**
     * provides the response type.
     *
     * If the endpoint has multiple responses and there is no result data type the response
     * type will be {@code Object}. If the response has a result data type the response type
     * will be {@ode ResultDataType<?>}.
     *
     * @return the response type
     */
    String getResponseType () {
        if (hasMultipleResponses ()) {
            multiResponseTypeName
        } else {
            singleResponseTypeName
        }
    }

    /**
     * provides the imports required for {@link #getResponseType()}.
     *
     * @return list of imports
     */
    Set<String> getResponseImports () {
        if (hasMultipleResponses ()) {
            multiImports
        } else {
            singleImports
        }
    }

    /**
     * returns a list with all content types.
     */
    List<String> getContentTypes () {
        def result = []

        if (main != null && main.contentType) {
            result.add (main.contentType)
        }

        errors.each {
            result.addAll (it.contentType)
        }
        result
    }

    /**
     * can this response return multiple types?
     *
     * @return true if multi else false
     */
    private boolean hasMultipleResponses () {
        def responseType = main.responseType

        if (responseType.isMultiOf ()) {
            return true
        }

        !errors.empty
    }

    private String getMultiResponseTypeName () {
        def rt = asResultDataType ()
        if (rt) {
            rt.nameMulti
        } else {
            'Object'
        }
    }

    private String getSingleResponseTypeName () {
        main.responseType.name
    }

    private Set<String> getMultiImports () {
        def rt = asResultDataType ()
        if (rt) {
            rt.importsMulti
        } else {
            [] as Set<String>
        }
    }

    private Set<String> getSingleImports () {
        def imports = [] as Set<String>

        imports.addAll (main.imports)
        errors.each {
            imports.addAll (it.imports)
        }

        imports
    }

    private ResultDataType asResultDataType () {
        def rt = main.responseType
        rt instanceof ResultDataType ? rt : null
    }

}
