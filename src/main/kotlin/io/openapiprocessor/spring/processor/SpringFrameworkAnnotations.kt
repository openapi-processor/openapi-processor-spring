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

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.framework.FrameworkAnnotation
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.MultipartParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * provides Spring annotation details.
 *
 * @author Martin Hauner
 */
class SpringFrameworkAnnotations: FrameworkAnnotations {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getAnnotation(httpMethod: HttpMethod): FrameworkAnnotation {
        return MAPPING_ANNOTATIONS[httpMethod]!!
    }

    override fun getAnnotation(parameter: Parameter): FrameworkAnnotation {
        return when(parameter) {
            is RequestBody -> getAnnotation("body")
            is PathParameter -> getAnnotation("path")
            is QueryParameter -> getAnnotation("query")
            is HeaderParameter -> getAnnotation("header")
            is CookieParameter -> getAnnotation("cookie")
            is MultipartParameter -> getAnnotation("multipart")
            else -> {
                log.error("unknown parameter type: ${parameter::class.java.name}")
                UNKNOWN_ANNOTATION
            }
        }
    }

    private fun getAnnotation(key: String): FrameworkAnnotation {
        return PARAMETER_ANNOTATIONS[key]!!
    }

}

private const val ANNOTATION_PKG = "org.springframework.web.bind.annotation"

private val MAPPING_ANNOTATIONS = hashMapOf(
    HttpMethod.DELETE  to FrameworkAnnotation("DeleteMapping", ANNOTATION_PKG),
    HttpMethod.GET     to FrameworkAnnotation("GetMapping", ANNOTATION_PKG),
    HttpMethod.HEAD    to FrameworkAnnotation("HeadMapping", ANNOTATION_PKG),
    HttpMethod.OPTIONS to FrameworkAnnotation("OptionsMapping", ANNOTATION_PKG),
    HttpMethod.PATCH   to FrameworkAnnotation("PatchMapping", ANNOTATION_PKG),
    HttpMethod.POST    to FrameworkAnnotation("PostMapping", ANNOTATION_PKG),
    HttpMethod.PUT     to FrameworkAnnotation("PutMapping", ANNOTATION_PKG),
    HttpMethod.TRACE   to FrameworkAnnotation("TraceMapping", ANNOTATION_PKG)
)

private val PARAMETER_ANNOTATIONS = hashMapOf(
    "query"     to FrameworkAnnotation ("RequestParam", ANNOTATION_PKG),
    "header"    to FrameworkAnnotation ("RequestHeader", ANNOTATION_PKG),
    "cookie"    to FrameworkAnnotation ("CookieValue", ANNOTATION_PKG),
    "path"      to FrameworkAnnotation ("PathVariable", ANNOTATION_PKG),
    "multipart" to FrameworkAnnotation ("RequestParam", ANNOTATION_PKG),
    "body"      to FrameworkAnnotation ("RequestBody", ANNOTATION_PKG)
)

private val UNKNOWN_ANNOTATION = FrameworkAnnotation("Unknown", "fix.me")
