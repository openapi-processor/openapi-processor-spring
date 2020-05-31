/*
 * Copyright 2020 the original authors
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

package com.github.hauner.openapi.micronaut.processor

import com.github.hauner.openapi.core.framework.FrameworkAnnotation
import com.github.hauner.openapi.core.framework.FrameworkAnnotations
import com.github.hauner.openapi.core.model.parameters.CookieParameter
import com.github.hauner.openapi.core.model.parameters.HeaderParameter
import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.core.model.parameters.QueryParameter
import groovy.util.logging.Slf4j

/**
 * provides Micronaut annotation details.
 *
 * @author Martin Hauner
 */
@Slf4j
class MicronautFrameworkAnnotations implements FrameworkAnnotations {
    private static ANNOTATION_PKG = 'io.micronaut.http.annotation'

    def MAPPING_ANNOTATIONS = [
        (HttpMethod.DELETE) : new FrameworkAnnotation (name: 'Delete', pkg: ANNOTATION_PKG),
        (HttpMethod.GET)    : new FrameworkAnnotation (name: 'Get', pkg: ANNOTATION_PKG),
        (HttpMethod.HEAD)   : new FrameworkAnnotation (name: 'Head', pkg: ANNOTATION_PKG),
        (HttpMethod.OPTIONS): new FrameworkAnnotation (name: 'Options', pkg: ANNOTATION_PKG),
        (HttpMethod.PATCH)  : new FrameworkAnnotation (name: 'Patch', pkg: ANNOTATION_PKG),
        (HttpMethod.POST)   : new FrameworkAnnotation (name: 'Post', pkg: ANNOTATION_PKG),
        (HttpMethod.PUT)    : new FrameworkAnnotation (name: 'Put', pkg: ANNOTATION_PKG),
        (HttpMethod.TRACE)  : new FrameworkAnnotation (name: 'Trace', pkg: ANNOTATION_PKG)
    ]

    def PARAMETER_ANNOTATIONS = [
        query : new FrameworkAnnotation (name: 'QueryValue', pkg: ANNOTATION_PKG),
        header: new FrameworkAnnotation (name: 'Header', pkg: ANNOTATION_PKG),
        cookie: new FrameworkAnnotation (name: 'CookieValue', pkg: ANNOTATION_PKG)
    ]

    def UNKNOWN_ANNOTATION = new FrameworkAnnotation(name: 'Unknown', pkg: 'fix.me')

    @Override
    FrameworkAnnotation getAnnotation (HttpMethod httpMethod) {
        MAPPING_ANNOTATIONS[httpMethod]
    }

    @Override
    FrameworkAnnotation getAnnotation (Parameter parameter) {
        switch (parameter) {
            case {it instanceof QueryParameter}:
                return PARAMETER_ANNOTATIONS['query']
            case {it instanceof HeaderParameter}:
                return PARAMETER_ANNOTATIONS['header']
            case {it instanceof CookieParameter}:
                return PARAMETER_ANNOTATIONS['cookie']
            default:
                log.error ("unknown parameter type: ${parameter.class.name}")
                return UNKNOWN_ANNOTATION
        }
    }

}
