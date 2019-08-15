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
import com.github.hauner.openapi.spring.model.Schema
import spock.lang.Specification

class MethodWriterSpec extends Specification {
    def writer = new MethodWriter ()
    def target = new StringWriter ()

    void "writes parameter less method without response" () {
        def endpoint = new Endpoint (path: '/ping', method: HttpMethod.GET, responses: [
            new Response(responseType: new Schema(type: 'none'))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}")
    ResponseEntity<void> getPing();
"""
    }

    void "writes parameter less method with string response type" () {
        def endpoint = new Endpoint (path: '/string', method: HttpMethod.GET, responses: [
            new Response(contentType: 'text/plain',
                responseType: new Schema(type: 'string'))
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
                responseType: new Schema(type: 'integer', format: 'int32'))
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
                responseType: new Schema(type: 'integer', format: 'int64'))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Long> getLong();
"""
    }

    void "writes parameter less method with integer response type when no format is given" () {
        def endpoint = new Endpoint (path: '/integer', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.integer',
                responseType: new Schema(type: 'integer'))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Integer> getInteger();
"""
    }

    void "writes parameter less method with float response type" () {
        def endpoint = new Endpoint (path: '/float', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.float',
                responseType: new Schema(type: 'number', format: 'float'))
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
                responseType: new Schema(type: 'number', format: 'double'))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Double> getDouble();
"""
    }

    void "writes parameter less method with number response type when no format is given" () {
        def endpoint = new Endpoint (path: '/float', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.float',
                responseType: new Schema(type: 'number'))
        ])

        when:
        writer.write (target, endpoint)

        then:
        target.toString () == """\
    @GetMapping(path = "${endpoint.path}", produces = {"${endpoint.response.contentType}"})
    ResponseEntity<Float> getFloat();
"""
    }

    void "writes parameter less method with boolean response type" () {
        def endpoint = new Endpoint (path: '/boolean', method: HttpMethod.GET, responses: [
            new Response(contentType: 'application/vnd.boolean',
                responseType: new Schema(type: 'boolean'))
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
                responseType: new Schema(type: 'map'))
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
