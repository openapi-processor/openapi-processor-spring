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


import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.spring.model.datatypes.DataTypeHelper
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType

import com.github.hauner.openapi.spring.model.parameters.CookieParameter
import com.github.hauner.openapi.spring.model.parameters.HeaderParameter
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import spock.lang.Specification
import spock.lang.Unroll

class MethodGeneratorSpec extends Specification {
    def apiOptions = new ApiOptions()
    def writer = new MethodGenerator (apiOptions: apiOptions)

    void "writes parameter less method without response" () {
        def endpoint = new Endpoint (path: '/ping', method: HttpMethod.GET, responses: [
            new Response(responseType: DataTypeHelper.createVoid ())
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == 'getPing'
        methodSpec.parameters.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == 'java.lang.Void'
    }

    @Unroll
    void "writes parameter less method with simple data type #type" () {
        def endpoint = new Endpoint (path: "/$type", method: HttpMethod.GET, responses: [
            new Response(contentType: contentType,
                responseType: responseType)
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "get${type.capitalize ()}"
        methodSpec.parameters.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.annotations[0].members.get ('produces')[0].toString () == "{\"${endpoint.response.contentType}\"}"
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.${type.capitalize ()}"

        where:
        type      | contentType               | responseType
        'string'  | 'text/plain'              | DataTypeHelper.createString (null)
        'integer' | 'application/vnd.integer' | DataTypeHelper.createInteger (null)
        'long'    | 'application/vnd.long'    | DataTypeHelper.createLong (null)
        'float'   | 'application/vnd.float'   | DataTypeHelper.createFloat (null)
        'double'  | 'application/vnd.double'  | DataTypeHelper.createDouble (null)
        'boolean' | 'application/vnd.boolean' | DataTypeHelper.createBoolean (null)
    }

    void "writes parameter less method with inline object response type" () {
        def endpoint = new Endpoint (path: '/inline', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json',
                responseType: new ObjectDataType (
                    name: 'GetInlineResponse', packageName: 'test', properties: [
                    foo1: DataTypeHelper.createString (null),
                    foo2: DataTypeHelper.createString (null)
                ]))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getInline"
        methodSpec.parameters.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.annotations[0].members.get ('produces')[0].toString () == "{\"${endpoint.response.contentType}\"}"
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "test.GetInlineResponse"
    }

    void "writes method with Collection response type" () {
        def endpoint = new Endpoint (path: '/collection', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: DataTypeHelper.createCollection (null, DataTypeHelper.createString (null)))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getCollection"
        methodSpec.parameters.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.annotations[0].members.get ('produces')[0].toString () == "{\"${endpoint.response.contentType}\"}"
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].rawType.canonicalName == "java.util.Collection"
        methodSpec.returnType.typeArguments[0].typeArguments.size() == 1
        methodSpec.returnType.typeArguments[0].typeArguments[0].canonicalName == "java.lang.String"
    }

    void "writes method with List response type" () {
        def endpoint = new Endpoint (path: '/list', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: DataTypeHelper.createList (null, DataTypeHelper.createString (null)))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getList"
        methodSpec.parameters.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.annotations[0].members.get ('produces')[0].toString () == "{\"${endpoint.response.contentType}\"}"
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].rawType.canonicalName == "java.util.List"
        methodSpec.returnType.typeArguments[0].typeArguments.size() == 1
        methodSpec.returnType.typeArguments[0].typeArguments[0].canonicalName == "java.lang.String"
    }

    void "writes method with Set response type" () {
        def endpoint = new Endpoint (path: '/set', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: DataTypeHelper.createSet (null, DataTypeHelper.createString (null)))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getSet"
        methodSpec.parameters.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.annotations[0].members.get ('produces')[0].toString () == "{\"${endpoint.response.contentType}\"}"
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].rawType.canonicalName == "java.util.Set"
        methodSpec.returnType.typeArguments[0].typeArguments.size() == 1
        methodSpec.returnType.typeArguments[0].typeArguments[0].canonicalName == "java.lang.String"
    }

    void "writes simple (required) query parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: 'foo', required: true, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestParam'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"foo"'
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes simple (optional) query parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestParam'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"foo"'
        methodSpec.parameters[0].annotations[0].members.get ('required')[0].toString () == 'false'
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes simple (required) header parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new HeaderParameter(name: 'x-foo', required: true, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'xFoo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestHeader'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"x-foo"'
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes simple (optional) header parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new HeaderParameter(name: 'x-foo', required: false, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'xFoo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestHeader'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"x-foo"'
        methodSpec.parameters[0].annotations[0].members.get ('required')[0].toString () == 'false'
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes simple (required) cookie parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new CookieParameter(name: 'foo', required: true, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.CookieValue'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"foo"'
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes simple (optional) cookie parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new CookieParameter(name: 'foo', required: false, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.CookieValue'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"foo"'
        methodSpec.parameters[0].annotations[0].members.get ('required')[0].toString () == 'false'
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes object query parameter without @RequestParam annotation" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new ObjectDataType (
                name: 'Foo', packageName: 'test', properties: [
                    foo1: DataTypeHelper.createString (null),
                    foo2: DataTypeHelper.createString (null)
                ]
            ))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.canonicalName == 'test.Foo'
        methodSpec.parameters[0].annotations.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes map from single query parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false, dataType: new MappedDataType (
                name: 'Map',
                packageName: 'java.util',
                generics: [
                    DataTypeHelper.createString (null),
                    DataTypeHelper.createString (null)
                ]
            ))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.rawType.canonicalName == 'java.util.Map'
        methodSpec.parameters[0].type.typeArguments.size() == 2
        methodSpec.parameters[0].type.typeArguments[0].canonicalName == 'java.lang.String'
        methodSpec.parameters[0].type.typeArguments[1].canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestParam'
        methodSpec.parameters[0].annotations[0].members.size () == 0
        methodSpec.annotations.size () == 1
        methodSpec.annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.GetMapping'
        methodSpec.annotations[0].members.get ('path')[0].toString () == "\"${endpoint.path}\""
        methodSpec.returnType.rawType.canonicalName == 'org.springframework.http.ResponseEntity'
        methodSpec.returnType.typeArguments. size() == 1
        methodSpec.returnType.typeArguments[0].canonicalName == "java.lang.Void"
    }

    void "writes method name from path with valid java identifiers" () {
        def endpoint = new Endpoint (path: '/f_o-ooo/b_a-rrr', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: 'foo', required: true, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFOOooBARrr"
        methodSpec.annotations[0].members.get ('path')[0].toString () == '"/f_o-ooo/b_a-rrr"'
    }

    void "writes method parameter with valid java identifiers" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: '_fo-o', required: true, dataType: DataTypeHelper.createString (null))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters[0].name == 'foO'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"_fo-o"'
    }

    void "writes required request body parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.POST, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], requestBodies: [
            new RequestBody(
                contentType: 'application/json',
                requestBodyType: new ObjectDataType (name: 'FooRequestBody', packageName: 'test',
                    properties: ['foo': DataTypeHelper.createString (null)] as LinkedHashMap),
                required: true)
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "postFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'body'
        methodSpec.parameters[0].type.canonicalName == 'test.FooRequestBody'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestBody'
        methodSpec.parameters[0].annotations[0].members.size () == 0
    }

    void "writes optional request body parameter" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.POST, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], requestBodies: [
            new RequestBody(
                contentType: 'application/json',
                requestBodyType: new ObjectDataType (
                    name: 'FooRequestBody',
                    packageName: 'test',
                    properties: ['foo': DataTypeHelper.createString (null)] as LinkedHashMap),
                required: false)
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "postFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'body'
        methodSpec.parameters[0].type.canonicalName == 'test.FooRequestBody'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestBody'
        methodSpec.parameters[0].annotations[0].members.size () == 1
        methodSpec.parameters[0].annotations[0].members.get ('required')[0].toString () == 'false'
    }

    void "writes simple (optional) parameter with default value" () {
        def endpoint = new Endpoint (path: '/foo', method: HttpMethod.GET, responses: [
            new Response (contentType: 'application/json', responseType: DataTypeHelper.createVoid ())
        ], parameters: [
            new QueryParameter(name: 'foo', required: false,
                dataType: DataTypeHelper.createString (new DataTypeConstraints (defaultValue: 'bar')))
        ])

        when:
        def methodSpec = writer.generateMethodSpec (endpoint)

        then:
        methodSpec.name == "getFoo"
        methodSpec.parameters.size () == 1
        methodSpec.parameters[0].name == 'foo'
        methodSpec.parameters[0].type.canonicalName == 'java.lang.String'
        methodSpec.parameters[0].annotations.size () == 1
        methodSpec.parameters[0].annotations[0].type.canonicalName == 'org.springframework.web.bind.annotation.RequestParam'
        methodSpec.parameters[0].annotations[0].members.get ('name')[0].toString () == '"foo"'
        methodSpec.parameters[0].annotations[0].members.get ('required')[0].toString () == 'false'
        methodSpec.parameters[0].annotations[0].members.get ('defaultValue')[0].toString () == '"bar"'
    }
}
