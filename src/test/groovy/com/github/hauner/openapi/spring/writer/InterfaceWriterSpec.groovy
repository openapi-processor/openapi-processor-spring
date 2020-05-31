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

import com.github.hauner.openapi.core.framework.FrameworkImports
import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.EndpointResponse
import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.spring.model.Interface
import com.github.hauner.openapi.spring.model.RequestBody
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.ResultDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.processor.SpringFrameworkImports
import com.github.hauner.openapi.spring.support.EmptyResponse
import spock.lang.Specification

import java.util.stream.Collectors

import static com.github.hauner.openapi.spring.support.AssertHelper.extractImports


class InterfaceWriterSpec extends Specification {
    def headerWriter = Mock HeaderWriter
    def methodWriter = Stub MethodWriter
    def apiOptions = new ApiOptions()

    def writer = new InterfaceWriter(
        headerWriter: headerWriter,
        methodWriter: methodWriter,
        frameworkImports: new SpringFrameworkImports(),
        apiOptions: apiOptions)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def apiItf = new Interface ()

        when:
        writer.write (target, apiItf)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def apiItf = new Interface (pkg: pkg)

        when:
        writer.write (target, apiItf)

        then:
        target.toString ().contains (
"""\
package $pkg;

""")
    }

    void "writes GetMapping import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [new EmptyResponse()]])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.web.bind.annotation.GetMapping;
""")
    }

    void "writes mapping imports" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.PUT, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.POST, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.web.bind.annotation.GetMapping;
""")
        result.contains("""\
import org.springframework.web.bind.annotation.PutMapping;
""")
        result.contains("""\
import org.springframework.web.bind.annotation.PostMapping;
""")
    }

    void "writes result data type import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [
                    new Response (responseType:
                        new ResultDataType (
                            type: 'ResponseEntity',
                            pkg: 'org.springframework.http',
                            dataType: new NoneDataType ()
                        ))
                ]]).initEndpointResponses ()
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.http.ResponseEntity;
""")
    }

    void "writes @RequestParam import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]],
                parameters: [
                    new QueryParameter(name: 'any', dataType: new StringDataType())
                ])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.web.bind.annotation.RequestParam;
""")
    }

    void "does not write @RequestParam annotation import of parameter that does not want the annotation" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new Response (contentType: 'application/json', responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                type: 'Foo', properties: [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ]
            ))
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        ! result.contains("""\
import org.springframework.web.bind.annotation.RequestParam;
""")
    }

    void "writes import of request parameter data type" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new Response (contentType: 'application/json', responseType: new NoneDataType())]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                pkg: 'model', type: 'Foo', properties: [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ]
            ))
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import model.Foo;
""")
    }

    void "writes @RequestBody import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: '/foo', method: HttpMethod.GET, responses: [
                '200': [new EmptyResponse()]
            ], requestBodies: [
                new RequestBody (
                    contentType: 'plain/text',
                    requestBodyType: new StringDataType (),
                    required: true
                )
            ])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.web.bind.annotation.RequestBody;
""")
    }

    void "writes import of request body data type" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '200': [new EmptyResponse ()]
        ], requestBodies: [
            new RequestBody (
                contentType: 'plain/text',
                requestBodyType: new MappedDataType (
                    pkg: 'com.github.hauner.openapi', type: 'Bar'),
                required: true
            )
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import com.github.hauner.openapi.Bar;
""")
    }

    void "writes model import"() {
        def pkg = 'model.package'
        def type = 'Model'

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [
                    new Response (
                        contentType: 'application/json',
                        responseType: new ObjectDataType (type: type, pkg: pkg))
                ]
            ]).initEndpointResponses ()
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import ${pkg}.${type};
""")
    }

    //@Ignore
    void "writes multiple response model import"() {
        def pkg = 'model.package'
        def type = 'Model'

        def pkg2 = 'model.package2'
        def type2 = 'Model2'

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint (path: 'path', method: HttpMethod.GET, responses: [
                '200': [
                    new Response (
                        contentType: 'application/json',
                        responseType: new ObjectDataType (type: type, pkg: pkg)),
                    new Response (
                        contentType: 'text/plain',
                        responseType: new ObjectDataType (type: type2, pkg: pkg2))
                ]
            ]).initEndpointResponses ()
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import ${pkg}.${type};
""")
        result.contains("""\
import ${pkg2}.${type2};
""")
    }

    void "sorts imports as strings"() {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.PUT, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.POST, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
""")
    }

    void "filters unnecessary 'java.lang' imports"() {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                '200': [new Response(contentType: 'plain/text', responseType: new StringDataType())]
            ])
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        !result.contains("""\
import java.lang.String;
""")
    }

    void "writes 'interface' block" () {
        def apiItf = new Interface (name: 'name', endpoints: [])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBlock(target.toString ())
        result == """\
public interface NameApi {
}
"""
    }

    void "writes methods" () {
        def endpoints = [
            new Endpoint(path: 'path1', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]])
                .initEndpointResponses (),
            new Endpoint(path: 'path2', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]])
                .initEndpointResponses ()
        ]

        writer.methodWriter.write (_ as Writer, _ as Endpoint, _ as EndpointResponse) >> {
            Writer target = it.get (0)
            Endpoint e = it.get (1)
            target.write ("// ${e.path}\n")
        }

        def apiItf = new Interface (name: 'name', endpoints: endpoints)

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBody(target.toString ())
        result == """\

// path1

// path2

"""
    }

    String extractInterfaceBlock (String source) {
        source.readLines ().stream ()
            .filter {it ==~ /public interface (.+?) \{/ || it ==~ /\}/}
            .collect (Collectors.toList ())
            .join ('\n') + '\n'
    }

    String extractInterfaceBody (String source) {
        source
            .replaceFirst (/(?s)(.*?)interface (.+?) \{\n/, '')
            .replaceFirst (/(?s)\}\n/, '')
    }
}
