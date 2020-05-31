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

import com.github.hauner.openapi.spring.converter.ApiOptions
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
import com.github.hauner.openapi.spring.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.ResultDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.parameters.CookieParameter
import com.github.hauner.openapi.spring.model.parameters.HeaderParameter
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.processor.SpringFrameworkAnnotations
import spock.lang.Specification
import spock.lang.Unroll

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

    void "writes parameter less method without response" () {
        def endpoint = createEndpoint (path: '/ping', method: HttpMethod.GET, responses: [
            '204': [new Response(responseType: new NoneDataType())]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getPing();
"""
    }

    @Unroll
    void "writes parameter less method with simple data type #type" () {
        def endpoint = createEndpoint (path: "/$type", method: HttpMethod.GET, responses: [
            '200': [new Response(contentType: contentType, responseType: responseType)]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        def rsp = endpoint.getFirstResponse ('200')
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${rsp.contentType}"})
    ${type.capitalize ()} get${type.capitalize ()}();
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
        def endpoint = createEndpoint (path: '/inline', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json',
                    responseType: new ObjectDataType (
                        type: 'GetInlineResponse', properties: [
                        foo1: new StringDataType (),
                        foo2: new StringDataType ()
                    ]))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        def rsp = endpoint.getFirstResponse ('200')
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${rsp.contentType}"})
    GetInlineResponse getInline();
"""
    }

    void "writes method with Collection response type" () {
        def endpoint = createEndpoint (path: '/collection', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json',
                    responseType: new CollectionDataType (item: new StringDataType ()))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        def rsp = endpoint.getFirstResponse ('200')
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${rsp.contentType}"})
    Collection<String> getCollection();
"""
    }

    void "writes method with List response type" () {
        def endpoint = createEndpoint (path: '/list', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json',
                    responseType: new ListDataType (item: new StringDataType ()))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        def rsp = endpoint.getFirstResponse ('200')
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${rsp.contentType}"})
    List<String> getList();
"""
    }

    void "writes method with Set response type" () {
        def endpoint = createEndpoint (path: '/set', method: HttpMethod.GET, responses: [
            '200': [
                new Response (contentType: 'application/json',
                    responseType: new SetDataType (item: new StringDataType ()))
            ]
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        def rsp = endpoint.getFirstResponse ('200')
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${rsp.contentType}"})
    Set<String> getSet();
"""
    }

    // todo core: check method writer calls parameter annotation writer

    @Deprecated
    void "writes simple (required) query parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType ())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam(name = "foo") String foo);
"""
    }

    @Deprecated
    void "writes simple (optional) query parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType ())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam(name = "foo", required = false) String foo);
"""
    }

    void "writes simple (required) header parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())
            ]
        ], parameters: [
            new HeaderParameter(name: 'x-foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestHeader(name = "x-foo") String xFoo);
"""
    }

    void "writes simple (optional) header parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new HeaderParameter(name: 'x-foo', required: false, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestHeader(name = "x-foo", required = false) String xFoo);
"""
    }

    void "writes simple (required) cookie parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new CookieParameter(name: 'foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@CookieValue(name = "foo") String foo);
"""
    }

    void "writes simple (optional) cookie parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new CookieParameter(name: 'foo', required: false, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@CookieValue(name = "foo", required = false) String foo);
"""
    }

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

    void "writes map from single query parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
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

    void "writes method name from path with valid java identifiers" () {
        def endpoint = createEndpoint (path: '/f_o-ooo/b_a-rrr', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFOOooBARrr(@RequestParam(name = "foo") String foo);
"""
    }

    void "writes method name from operation id with valid java identifiers" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, operationId: 'get-bar',
            responses: [
                '204': [new Response (responseType: new NoneDataType())]
            ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getBar();
"""
    }

    void "writes method parameter with valid java identifiers" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: '_fo-o', required: true, dataType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam(name = "_fo-o") String foO);
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

    void "writes simple (optional) parameter with string default value" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false,
                dataType: new StringDataType(
                    constraints: new DataTypeConstraints (defaultValue: 'bar')))
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam(name = "foo", required = false, defaultValue = "bar") String foo);
"""
    }

    void "writes simple (optional) parameter with number default value" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new Response (responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false,
                dataType: new LongDataType (
                    constraints: new DataTypeConstraints (defaultValue: 5)))
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam(name = "foo", required = false, defaultValue = "5") Long foo);
"""
    }

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
