/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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
import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.InlineObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
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
    ResponseEntity<void> getPing();
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
        type     | contentType  | responseType
        'string' | 'text/plain' | new StringDataType ()
    }

    void "writes parameter less method with string response type" () {
        def endpoint = new Endpoint (path: '/string', method: HttpMethod.GET, responses: [
            new Response(contentType: 'text/plain',
                responseType: new StringDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<String> getString();
"""
    }

    void "writes parameter less method with integer response type" () {
        def endpoint = new Endpoint (path: '/integer', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.integer',
                responseType: new IntegerDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Integer> getInteger();
"""
    }

    void "writes parameter less method with long response type" () {
        def endpoint = new Endpoint (path: '/long', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.long',
                responseType: new LongDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Long> getLong();
"""
    }

    void "writes parameter less method with float response type" () {
        def endpoint = new Endpoint (path: '/float', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.float',
                responseType: new FloatDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Float> getFloat();
"""
    }

    void "writes parameter less method with double response type" () {
        def endpoint = new Endpoint (path: '/double', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.double',
                responseType: new DoubleDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Double> getDouble();
"""
    }

    void "writes parameter less method with boolean response type" () {
        def endpoint = new Endpoint (path: '/boolean', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.boolean',
                responseType: new BooleanDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Boolean> getBoolean();
"""
    }

    void "writes parameter less method with inline object response type" () {
        def endpoint = new Endpoint (path: '/inline', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/json',
                responseType: new InlineObjectDataType())
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Map<String, Object>> getInline();
"""
    }
}
