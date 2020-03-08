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



    String getResponseType () {
        if (hasMultipleResponses ()) {
            '?'
        } else {
            main.responseType.name
        }
    }

    String getContentType () {
        main.contentType
    }

    /**
     * can this response return multiple types?
     *
     * @return true if multi else false
     */
    boolean hasMultipleResponses () {
        def responseType = main.responseType

        if (responseType.isMultiOf ()) {
            return true
        }


        !errors.empty
    }

    /**
     * provides the imports required for this response.
     *
     * @return list of imports
     */
    Set<String> getResponseImports () {
        if (errors.empty) {
            def imports = [] as Set<String>

            imports.addAll (main.imports)
            errors.each {
                imports.addAll (it.imports)
            }

            imports
        } else {
            // Because the response has multiple possible return types it will return "?" (any)
            // and no imports are required.
            [] as Set<String>
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

}
