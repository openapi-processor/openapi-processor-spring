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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.core.model.Endpoint
import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.model.RequestBody
import com.github.hauner.openapi.core.model.Response
import com.github.hauner.openapi.core.model.datatypes.CollectionDataType
import com.github.hauner.openapi.core.model.datatypes.NoneDataType
import com.github.hauner.openapi.core.model.datatypes.ResultDataType
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
        HttpMethod.GET     | "get-it"     | """@GetMapping(path = "get-it")"""
        HttpMethod.PUT     | "put-it"     | """@PutMapping(path = "put-it")"""
        HttpMethod.POST    | "post-it"    | """@PostMapping(path = "post-it")"""
        HttpMethod.DELETE  | "delete-it"  | """@DeleteMapping(path = "delete-it")"""
        HttpMethod.OPTIONS | "options-it" | """@OptionsMapping(path = "options-it")"""
        HttpMethod.HEAD    | "head-it"    | """@HeadMapping(path = "head-it")"""
        HttpMethod.PATCH   | "patch-it"   | """@PatchMapping(path = "patch-it")"""
        HttpMethod.TRACE   | "trace-it"   | """@TraceMapping(path = "trace-it")"""
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
        'plain/text'        | """@GetMapping(path = "/foo", consumes = {"plain/text"})"""
        'application/json'  | """@GetMapping(path = "/foo", consumes = {"application/json"})"""
    }

    void "writes 'produces' parameter with response content type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: contentType,
                    responseType: new StringDataType ())
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == expected

        where:
        contentType         | expected
        'plain/text'        | """@GetMapping(path = "/foo", produces = {"plain/text"})"""
        'application/json'  | """@GetMapping(path = "/foo", produces = {"application/json"})"""
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
        'foo/in'           | 'foo/out'           | """@GetMapping(path = "/foo", consumes = {"foo/in"}, produces = {"foo/out"})"""
    }

    void "writes mapping annotation with multiple result content types" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: 'application/json',
                    responseType: new StringDataType ())
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new StringDataType ())
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """@GetMapping(path = "${endpoint.path}", produces = {"${endpoint.responses.'200'.first ().contentType}", "${endpoint.responses.'default'.first ().contentType}"})"""
    }

    private Endpoint createEndpoint (Map properties) {
        new Endpoint(properties).initEndpointResponses ()
    }

}
