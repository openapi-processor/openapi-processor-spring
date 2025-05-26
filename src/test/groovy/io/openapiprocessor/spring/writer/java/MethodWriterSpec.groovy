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

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.Documentation
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.GenericDataType
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.writer.java.JavaDocWriter
import io.openapiprocessor.core.writer.java.JavaIdentifier
import io.openapiprocessor.spring.model.parameters.QueryParameter
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.EmptyResponse
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.MethodWriter
import spock.lang.Specification

class MethodWriterSpec extends Specification {
    def apiOptions = new ApiOptions()
    def identifier = new JavaIdentifier()

    def writer = new MethodWriter (
        apiOptions,
        identifier,
        new StatusAnnotationWriter(new SpringFrameworkAnnotations()),
        new MappingAnnotationWriter(new SpringFrameworkAnnotations()),
        new ParameterAnnotationWriter(new SpringFrameworkAnnotations()),
        new BeanValidationFactory (apiOptions),
        new JavaDocWriter(identifier))
    def target = new StringWriter ()

    @Deprecated
    private Endpoint createEndpoint (Map properties) {
        def ep = new Endpoint(
            properties.path as String ?: '',
            properties.method as HttpMethod ?: HttpMethod.GET,
            properties.parameters ?: [],
            properties.requestBodies ?: [],
            properties.responses ?: [:],
            properties.operationId as String ?: null,
            properties.deprecated as boolean ?: false,
            new Documentation(null, properties.description as String ?: null))
    }

    void "writes map from single query parameter" () {
        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.GET, responses: [
            '204': [new EmptyResponse()]
        ], parameters: [
            new QueryParameter('foo', new MappedDataType (
                'Map',
                'java.util',
                [
                    new GenericDataType(new DataTypeName("String", "String"), "java.lang", []),
                    new GenericDataType(new DataTypeName("String", "String"), "java.lang", [])
                ],
                null,
                false,
                null
            ), false, false, null)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping(path = "${endpoint.path}")
    void getFoo(@RequestParam Map<String, String> foo);
"""
    }

    void "head method" () {
        def dataTypeName = new DataTypeName('java.lang.String', 'java.lang.String')

        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.HEAD, responses: [
                '200': [new EmptyResponse()]
        ], parameters: [
                new QueryParameter('foo', new MappedDataType (
                        'String',
                        'java.util',
                        [],
                        null,
                        false,
                        null
                ), false, false, null)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @RequestMapping(path = "${endpoint.path}", method = RequestMethod.HEAD)
    void headFoo(@RequestParam(name = "foo", required = false) String foo);
"""
    }

    void "trace method" () {
        def dataTypeName = new DataTypeName('java.lang.String', 'java.lang.String')

        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.TRACE, responses: [
                '200': [new EmptyResponse()]
        ], parameters: [
                new QueryParameter('foo', new MappedDataType (
                        'String',
                        'java.util',
                        [],
                        null,
                        false,
                        null
                ), false, false, null)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @RequestMapping(path = "${endpoint.path}", method = RequestMethod.TRACE)
    void traceFoo(@RequestParam(name = "foo", required = false) String foo);
"""
    }

    void "option method" () {
        def dataTypeName = new DataTypeName('java.lang.String', 'java.lang.String')

        def endpoint = createEndpoint (path: '/foo', method: HttpMethod.OPTIONS, responses: [
                '200': [new EmptyResponse()]
        ], parameters: [
                new QueryParameter('foo', new MappedDataType (
                        'String',
                        'java.util',
                        [],
                        null,
                        false,
                        null
                ), false, false, null)
        ])

        when:
        writer.write (target, endpoint, endpoint.endpointResponses.first ())

        then:
        target.toString () == """\
    @RequestMapping(path = "${endpoint.path}", method = RequestMethod.OPTIONS)
    void optionsFoo(@RequestParam(name = "foo", required = false) String foo);
"""
    }

}
