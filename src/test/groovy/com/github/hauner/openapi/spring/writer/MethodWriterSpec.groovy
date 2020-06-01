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
import com.github.hauner.openapi.core.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import com.github.hauner.openapi.spring.processor.SpringFrameworkAnnotations
import spock.lang.Specification

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

}
