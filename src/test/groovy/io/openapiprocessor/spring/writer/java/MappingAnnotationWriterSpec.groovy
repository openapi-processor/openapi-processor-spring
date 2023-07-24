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

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.EmptyResponse
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.StringDataType
import spock.lang.Specification

class MappingAnnotationWriterSpec extends Specification {

    def writer = new MappingAnnotationWriter()
    def target = new StringWriter()

    void "writes http method specific mapping annotation" () {
        def endpoint = createEndpoint (path: path, method: httpMethod, responses: [
            '204' : [new EmptyResponse ()]
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
        HttpMethod.OPTIONS | "options-it" | """@RequestMapping(path = "options-it", method = RequestMethod.OPTIONS)"""
        HttpMethod.HEAD    | "head-it"    | """@RequestMapping(path = "head-it", method = RequestMethod.HEAD)"""
        HttpMethod.PATCH   | "patch-it"   | """@PatchMapping(path = "patch-it")"""
        HttpMethod.TRACE   | "trace-it"   | """@RequestMapping(path = "trace-it", method = RequestMethod.TRACE)"""
    }

    void "writes 'consumes' parameter with body content type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204' : [new EmptyResponse()]
        ], requestBodies: [
            new RequestBody('body', contentType, new StringDataType (), false, false)
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
            '200' : [new Response (contentType, new StringDataType (), null)]
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
                new Response (responseContentType, new StringDataType (), null)
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
        'foo/in'           | 'foo/out'           | """@GetMapping(path = "/foo", consumes = {"foo/in"}, produces = {"foo/out"})"""
    }

    void "writes mapping annotation with multiple result content types" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response ('application/json', new StringDataType (), null)
            ],
            'default': [
                new Response ('text/plain', new StringDataType (), null)
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """@GetMapping(path = "${endpoint.path}", produces = {"${endpoint.responses.'200'.first ().contentType}", "${endpoint.responses.'default'.first ().contentType}"})"""
    }

    void "writes unique 'consumes' parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204' : [new EmptyResponse ()]
        ], requestBodies: [
            new RequestBody('body', 'foo/in', new StringDataType (),
                false, false),
            new RequestBody('body', 'foo/in', new StringDataType (),
                false, false),
            new RequestBody('body', 'foo/in', new StringDataType (),
                false, false)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString ().contains ('consumes = {"foo/in"}')
    }

    void "writes unique 'produces' parameters" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response ('foo/out', new StringDataType (), null)
            ],
            '400' : [
                new Response ('foo/out', new StringDataType (), null)
            ],
            '401' : [
                new Response ('foo/out', new StringDataType (), null)
            ],
            '403': [
                new Response ('foo/out', new StringDataType (), null)
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString ().contains ('produces = {"foo/out"}')
    }

    @Deprecated
    private Endpoint createEndpoint (Map properties) {
        def ep = new Endpoint(
            properties.path as String ?: '',
            properties.method as HttpMethod ?: HttpMethod.GET,
            properties.operationId as String ?: null,
            properties.deprecated as boolean ?: false,
            properties.description as String ?: null
        )
        ep.parameters = properties.parameters ?: []
        ep.responses = properties.responses ?: [:]
        ep.requestBodies = properties.requestBodies ?: []
        ep.initEndpointResponses ()
    }

}
