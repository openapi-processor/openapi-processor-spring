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

package com.github.hauner.openapi.micronaut.writer

import com.github.hauner.openapi.core.model.Endpoint
import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.model.RequestBody
import com.github.hauner.openapi.core.model.Response
import com.github.hauner.openapi.core.model.datatypes.NoneDataType
import com.github.hauner.openapi.core.model.datatypes.StringDataType
import spock.lang.Specification

class MappingAnnotationWriterSpec extends Specification {
    def writer = new MappingAnnotationWriter()
    def target = new StringWriter()

    void "writes http method specific mapping annotation" () {
        def endpoint = createEndpoint (path: path, method: httpMethod, responses: [
            '204' : [new Response(responseType: new NoneDataType())]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == expected

        where:
        httpMethod         | path         | expected
        HttpMethod.GET     | "/get-it"     | """@Get(uri = "/get-it")"""
        HttpMethod.PUT     | "/put-it"     | """@Put(uri = "/put-it")"""
        HttpMethod.POST    | "/post-it"    | """@Post(uri = "/post-it")"""
        HttpMethod.DELETE  | "/delete-it"  | """@Delete(uri = "/delete-it")"""
        HttpMethod.OPTIONS | "/options-it" | """@Options(uri = "/options-it")"""
        HttpMethod.HEAD    | "/head-it"    | """@Head(uri = "/head-it")"""
        HttpMethod.PATCH   | "/patch-it"   | """@Patch(uri = "/patch-it")"""
        HttpMethod.TRACE   | "/trace-it"   | """@Trace(uri = "/trace-it")"""
    }

    void "writes 'consumes' parameter with body content type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204' : [new Response(responseType: new NoneDataType())]
        ], requestBodies: [
            new RequestBody(
                contentType: contentType,
                requestBodyType: new StringDataType ())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == expected

        where:
        contentType         | expected
        'plain/text'        | """@Get(uri = "/foo", consumes = {"plain/text"})"""
        'application/json'  | """@Get(uri = "/foo", consumes = {"application/json"})"""
    }

    void "writes 'produces' parameter with response content type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: contentType,
                    responseType: new StringDataType ())
            ],
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == expected

        where:
        contentType         | expected
        'plain/text'        | """@Get(uri = "/foo", produces = {"plain/text"})"""
        'application/json'  | """@Get(uri = "/foo", produces = {"application/json"})"""
    }

    void "writes 'consumes' & 'produces' parameters" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: responseContentType,
                    responseType: new StringDataType ())
            ]
        ], requestBodies: [
            new RequestBody(
                contentType: requestContentType,
                requestBodyType: new StringDataType ())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == expected

        where:
        requestContentType | responseContentType | expected
        'foo/in'           | 'foo/out'           | """@Get(uri = "/foo", consumes = {"foo/in"}, produces = {"foo/out"})"""
    }

    private Endpoint createEndpoint (Map properties) {
        new Endpoint(properties).initEndpointResponses ()
    }

}
