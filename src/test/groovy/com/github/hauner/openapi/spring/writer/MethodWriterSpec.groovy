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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.core.test.EmptyResponse
import com.github.hauner.openapi.core.writer.MethodWriter
import com.github.hauner.openapi.core.converter.ApiOptions
import com.github.hauner.openapi.core.model.Endpoint
import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.model.RequestBody
import com.github.hauner.openapi.core.model.Response
import com.github.hauner.openapi.core.model.datatypes.CollectionDataType
import com.github.hauner.openapi.core.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.core.model.datatypes.NoneDataType
import com.github.hauner.openapi.core.model.datatypes.ObjectDataType
import com.github.hauner.openapi.core.model.datatypes.ResultDataType
import com.github.hauner.openapi.core.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.processor.SpringFrameworkAnnotations
import spock.lang.Specification

@Deprecated
class MethodWriterSpec extends Specification {
    def apiOptions = new ApiOptions()
    def writer = new MethodWriter (
        apiOptions: apiOptions,
        mappingAnnotationWriter: new MappingAnnotationWriter(),
        parameterAnnotationWriter: new ParameterAnnotationWriter(
            annotations: new SpringFrameworkAnnotations()
        ))
    def target = new StringWriter ()

    private Endpoint createEndpoint (Map properties) {
        new Endpoint(properties).initEndpointResponses ()
    }

    // spring
    void "writes object query parameter without @RequestParam annotation" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                type: 'Foo', properties: [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ]
            ))
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(Foo foo);
"""
    }

    // spring => core?
    void "writes map from single query parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse ()]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new MappedMapDataType (
                type: 'Map',
                pkg: 'java.util',
                genericTypes: ['java.lang.String', 'java.lang.String']
            ))
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam Map<String, String> foo);
"""
    }

    void "writes required request body parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.POST, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], requestBodies: [
            new RequestBody(
                contentType: 'application/json',
                requestBodyType: new ObjectDataType (type: 'FooRequestBody',
                    properties: ['foo': new StringDataType ()] as LinkedHashMap),
                required: true)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @PostMapping(path = "${endpoint.path}", consumes = {"application/json"})
    void postFoo(@RequestBody FooRequestBody body);
"""
    }

    void "writes optional request body parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.POST, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], requestBodies: [
            new RequestBody(
                contentType: 'application/json',
                requestBodyType: new ObjectDataType (
                    type: 'FooRequestBody',
                    properties: ['foo': new StringDataType ()] as LinkedHashMap),
                required: false)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @PostMapping(path = "${endpoint.path}", consumes = {"application/json"})
    void postFoo(@RequestBody(required = false) FooRequestBody body);
"""
    }

    // core
    void "writes mapping annotation with multiple result content types" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: 'application/json',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString ().contains ("""\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.responses.'200'.first ().contentType}", "${endpoint.responses.'default'.first ().contentType}"})
""")
    }

    // core
    void "writes method with any response type when it has multiple result contents with default result type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: 'application/json',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.responses.'200'.first ().contentType}", "${endpoint.responses.'default'.first ().contentType}"})
    Object getFoo();
"""
    }

    // core
    void "writes method with any response type when it has multiple result contents with wrapped result type" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200' : [
                new Response (contentType: 'application/json',
                    responseType: new ResultDataType (
                        type: 'ResponseEntity',
                        pkg: 'org.springframework.http',
                        dataType: new CollectionDataType (item: new StringDataType ())))
            ],
            'default': [
                new Response (contentType: 'text/plain',
                    responseType: new ResultDataType (
                        type: 'ResponseEntity',
                        pkg: 'org.springframework.http',
                        dataType: new CollectionDataType (item: new StringDataType ())))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.responses.'200'.first ().contentType}", "${endpoint.responses.'default'.first ().contentType}"})
    ResponseEntity<?> getFoo();
"""
    }

    // core
    void "writes method with wrapped void response type" () {
        def endpoint = createEndpoint (path: '/ping', method: HttpMethod.GET, responses: [
            '204': [new Response(responseType:
                new ResultDataType (
                    type: 'ResponseEntity',
                    pkg: 'org.springframework.http',
                    dataType: new NoneDataType ().wrappedInResult ()
                ))]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getPing();
"""
    }

}
