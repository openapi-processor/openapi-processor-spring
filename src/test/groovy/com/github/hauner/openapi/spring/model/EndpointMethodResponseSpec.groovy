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

package com.github.hauner.openapi.spring.model

import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import spock.lang.Specification

class EndpointMethodResponseSpec extends Specification {

    void "creates single success/other content type groups" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200'    : [
                new Response (contentType: 'application/json',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ]
        ])

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 1
        result[0].main.contentType == 'application/json'
        result[0].errors as List == []
    }

    void "groups response content types to multiple success/other content type groups" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200'    : [
                new Response (contentType: 'application/json',
                    responseType: new CollectionDataType (item: new StringDataType ())),
                new Response (contentType: 'application/xml',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ]
        ])

        when:
        def result = endpoint.endpointResponses

        then:
        result.size () == 2
        result[0].main.contentType == 'application/json'
        result[0].errors.collect {it.contentType} == ['text/plain']
        result[1].main.contentType == 'application/xml'
        result[1].errors.collect {it.contentType} == ['text/plain']
    }

}
