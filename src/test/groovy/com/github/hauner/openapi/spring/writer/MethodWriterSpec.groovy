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

import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.spring.model.RequestBody
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.MapDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.parameters.CookieParameter
import com.github.hauner.openapi.spring.model.parameters.HeaderParameter
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import spock.lang.Specification
import spock.lang.Unroll

class MethodWriterSpec extends Specification {
    def writer = new MethodWriter ()
    def target = new StringWriter ()

    void "writes parameter less method without response" () {
        def endpoint = new Endpoint (path: '/ping', method: HttpMethod.GET, responses: [
            new Response(responseType: new NoneDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getPing();
"""
    }

    @Unroll
    void "writes parameter less method with simple data type #type" () {
        def endpoint = new Endpoint (path: "/$type", method: HttpMethod.GET, responses: [
            new Response(contentType: contentType,
                responseType: responseType)
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<${type.capitalize ()}> get${type.capitalize ()}();
"""

        where:
        type      | contentType               | responseType
        'string'  | 'text/plain'              | new StringDataType ()
        'integer' | 'application/vnd.integer' | new IntegerDataType ()
        'long'    | 'application/vnd.long'    | new LongDataType ()
        'float'   | 'application/vnd.float'   | new FloatDataType ()
        'double'  | 'application/vnd.double'  | new DoubleDataType ()
        'boolean' | 'application/vnd.boolean' | new BooleanDataType ()
    }

    void "writes parameter less method with inline object response type" () {
        def endpoint = new Endpoint (path: '/inline', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json',
                responseType: new ObjectDataType (
                    type: 'GetInlineResponse', properties: [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ]))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<GetInlineResponse> getInline();
"""
    }

    void "writes method with Collection response type" () {
        def endpoint = new Endpoint (path: '/collection', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: new CollectionDataType (item: new StringDataType()))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Collection<String>> getCollection();
"""
    }

    void "writes method with List response type" () {
        def endpoint = new Endpoint (path: '/list', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: new ListDataType (item: new StringDataType()))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<List<String>> getList();
"""
    }

    void "writes method with Set response type" () {
        def endpoint = new Endpoint (path: '/set', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: new SetDataType (item: new StringDataType()))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Set<String>> getSet();
"""
    }

    void "writes simple (required) query parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: 'foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestParam(name = "foo") String foo);
"""
    }

    void "writes simple (optional) query parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestParam(name = "foo", required = false) String foo);
"""
    }

    void "writes simple (required) header parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new HeaderParameter(name: 'x-foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestHeader(name = "x-foo") String xFoo);
"""
    }

    void "writes simple (optional) header parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new HeaderParameter(name: 'x-foo', required: false, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestHeader(name = "x-foo", required = false) String xFoo);
"""
    }

    void "writes simple (required) cookie parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new CookieParameter(name: 'foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@CookieValue(name = "foo") String foo);
"""
    }

    void "writes simple (optional) cookie parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new CookieParameter(name: 'foo', required: false, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@CookieValue(name = "foo", required = false) String foo);
"""
    }

    void "writes object query parameter without @RequestParam annotation" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                type: 'Foo', properties: [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ]
            ))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(Foo foo);
"""
    }

    void "writes map from single query parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new MapDataType ())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestParam(name = "foo") Map foo);
"""
    }

    void "writes method name from path with valid java identifiers" () {
        def endpoint = new Endpoint (path: '/f_o-ooo/b_a-rrr', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: 'foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFOOooBARrr(@RequestParam(name = "foo") String foo);
"""
    }

    void "writes method parameter with valid java identifiers" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: '_fo-o', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestParam(name = "_fo-o") String foO);
"""
    }

    void "writes required request body parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.POST, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], requestBodies: [
            new RequestBody(
                contentType: 'application/json',
                requestBodyType: new ObjectDataType (type: 'FooRequestBody',
                    properties: ['foo': new StringDataType ()] as LinkedHashMap),
                required: true)
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @PostMapping(path = "${endpoint.path}", consumes = {"application/json"})
    ResponseEntity<Void> postFoo(@RequestBody FooRequestBody body);
"""
    }

    void "writes optional request body parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.POST, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], requestBodies: [
            new RequestBody(
                contentType: 'application/json',
                requestBodyType: new ObjectDataType (
                    type: 'FooRequestBody',
                    properties: ['foo': new StringDataType ()] as LinkedHashMap),
                required: false)
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @PostMapping(path = "${endpoint.path}", consumes = {"application/json"})
    ResponseEntity<Void> postFoo(@RequestBody(required = false) FooRequestBody body);
"""
    }

    void "writes simple (optional) parameter with default value" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: new NoneDataType())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false,
                dataType: new StringDataType(
                    constraints: new DataTypeConstraints (defaultValue: 'bar')))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<Void> getFoo(@RequestParam(name = "foo", required = false, defaultValue = "bar") String foo);
"""
    }

}
