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
import com.github.hauner.openapi.spring.model.Interface
import com.github.hauner.openapi.spring.model.RequestBody
import com.github.hauner.openapi.spring.model.Response

import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.datatypes.VoidDataType
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.support.EmptyResponse
import spock.lang.Specification

class InterfaceGeneratorSpec extends Specification {
    def apiOptions = new ApiOptions()

    def writer = new InterfaceGenerator(methodWriter: new MethodGenerator(apiOptions: apiOptions), apiOptions: apiOptions)

    void "writes 'class'" () {
        def pkg = 'com.github.hauner.openapi'
        def apiItf = new Interface (pkg: pkg, name: "AnInterface")

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.name == 'AnInterfaceApi'
    }

    void "writes GetMapping" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'} != null
    }

    void "writes mappings" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.PUT, responses: ['200': [new EmptyResponse()]]),
            new Endpoint(path: 'path', method: HttpMethod.POST, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 3
        typeSpec.methodSpecs[0].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'} != null
        typeSpec.methodSpecs[1].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.PutMapping'} != null
        typeSpec.methodSpecs[2].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.PostMapping'} != null
    }

    void "writes ResponseEntity" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]])
        ])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        typeSpec.methodSpecs[0].returnType.typeArguments. size() == 1
        typeSpec.methodSpecs[0].returnType.typeArguments[0].canonicalName == 'java.lang.Void'
    }

    void "writes @RequestParam" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]],
                parameters: [
                    new QueryParameter(name: 'any', dataType: new StringDataType ())
                ])
        ])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].parameters.size () == 1
        typeSpec.methodSpecs[0].parameters[0].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.RequestParam'} != null
    }

    void "does not write @RequestParam annotation of parameter that does not want the annotation" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            ['200': [new Response (contentType: 'application/json', responseType: new VoidDataType ())]]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                name: 'Foo', packageName: 'bar', properties: [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ]
            ))
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].parameters.size () == 1
        typeSpec.methodSpecs[0].parameters[0].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.RequestParam'} == null
    }

    void "writes request parameter data type" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            ['200': [new Response (contentType: 'application/json', responseType: new VoidDataType ())]]
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                packageName: 'model', name: 'Foo', properties: [
                foo1: new StringDataType (),
                foo2: new StringDataType ()
            ]
            ))
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].parameters.size () == 1
        typeSpec.methodSpecs[0].parameters[0].type.canonicalName == 'model.Foo'
    }

    void "writes @RequestBody" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: '/foo', method: HttpMethod.GET, responses: ['200': [new EmptyResponse()]],
                requestBodies: [
                    new RequestBody(
                        contentType: 'plain/text',
                        requestBodyType: new StringDataType (),
                        required: true
                    )
                ])
        ])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].parameters.size () == 1
        typeSpec.methodSpecs[0].parameters[0].annotations.find {it.type.canonicalName == 'org.springframework.web.bind.annotation.RequestBody'} != null
    }

    void "writes request body data type" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            ['200': [new EmptyResponse ()]]
        ], requestBodies: [
            new RequestBody (
                contentType: 'plain/text',
                requestBodyType: new MappedDataType (
                    packageName: 'com.github.hauner.openapi', name: 'Bar'),
                required: true
            )
        ])

        def apiItf = new Interface (name: 'name', endpoints: [endpoint])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:
        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].parameters.size () == 1
        typeSpec.methodSpecs[0].parameters[0].type.canonicalName == 'com.github.hauner.openapi.Bar'
    }

    void "writes model import"() {
        def pkg = 'model.package'
        def type = 'Model'

        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET, responses: [
                ['200': [new Response(
                    contentType: 'application/json',
                    responseType: new ObjectDataType (name: type, packageName: pkg))
                ]]
            ]),
        ])

        when:
        def typeSpec = writer.generateTypeSpec (apiItf)

        then:

        typeSpec.methodSpecs.size () == 1
        typeSpec.methodSpecs[0].returnType.typeArguments.size() == 1
        typeSpec.methodSpecs[0].returnType.typeArguments[0].canonicalName == "${pkg}.${type}"
    }
}
