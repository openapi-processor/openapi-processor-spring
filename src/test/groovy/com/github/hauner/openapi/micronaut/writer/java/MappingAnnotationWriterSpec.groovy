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

package com.github.hauner.openapi.micronaut.writer.java

import com.github.hauner.openapi.core.model.Endpoint
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import spock.lang.Specification

class MappingAnnotationWriterSpec extends Specification {
    def writer = new MappingAnnotationWriter()
    def target = new StringWriter()

    void "writes http method specific mapping annotation" () {
        def endpoint = createEndpoint (path: path, method: httpMethod, responses: [
            '204' : [new Response("", new NoneDataType())]
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
            '204' : [new Response("", new NoneDataType())]
        ], requestBodies: [
            new RequestBody('body', contentType, new StringDataType (),
                false, false)
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
                new Response (contentType, new StringDataType ())
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
                new Response (responseContentType, new StringDataType ())
            ]
        ], requestBodies: [
            new RequestBody('body', requestContentType, new StringDataType (),
                false, false)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == expected

        where:
        requestContentType | responseContentType | expected
        'foo/in'           | 'foo/out'           | """@Get(uri = "/foo", consumes = {"foo/in"}, produces = {"foo/out"})"""
    }

    void "writes mapping annotation with multiple result content types" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response ('application/json', new StringDataType ())
            ],
            'default': [
                new Response ('text/plain', new StringDataType ())
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """@Get(uri = "${endpoint.path}", produces = {"${endpoint.responses.'200'.first ().contentType}", "${endpoint.responses.'default'.first ().contentType}"})"""
    }

    private Endpoint createEndpoint (Map properties) {
        new Endpoint(properties).initEndpointResponses ()
    }

}
