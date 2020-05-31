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

package com.github.hauner.openapi.spring.processor

import com.github.hauner.openapi.core.framework.FrameworkAnnotation
import com.github.hauner.openapi.core.framework.FrameworkAnnotations
import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.spring.model.parameters.QueryParameter
import groovy.util.logging.Slf4j

/**
 * provides Spring annotation details.
 *
 * @author Martin Hauner
 */
@Slf4j
class SpringFrameworkAnnotations implements FrameworkAnnotations {
    private static ANNOTATION_PKG = 'org.springframework.web.bind.annotation'

    def MAPPING_ANNOTATIONS = [
        (HttpMethod.DELETE) : new FrameworkAnnotation (name: 'DeleteMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.GET)    : new FrameworkAnnotation (name: 'GetMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.HEAD)   : new FrameworkAnnotation (name: 'HeadMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.OPTIONS): new FrameworkAnnotation (name: 'OptionsMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.PATCH)  : new FrameworkAnnotation (name: 'PatchMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.POST)   : new FrameworkAnnotation (name: 'PostMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.PUT)    : new FrameworkAnnotation (name: 'PutMapping', pkg: ANNOTATION_PKG),
        (HttpMethod.TRACE)  : new FrameworkAnnotation (name: 'TraceMapping', pkg: ANNOTATION_PKG)
    ]

    def PARAMETER_ANNOTATIONS = [
        query: new FrameworkAnnotation (name: 'RequestParam', pkg: ANNOTATION_PKG)
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
            default:
                def pkg = parameter.annotationWithPackage
                    .substring (0, parameter.annotationWithPackage.lastIndexOf ('.'))

                return new FrameworkAnnotation(name: parameter.annotationName, pkg: pkg)

//                log.error ("unknown parameter type: ${parameter.class.name}")
//                return UNKNOWN_ANNOTATION
        }
    }

}
