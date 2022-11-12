/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.framework.FrameworkAnnotation
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.logging.Logger
import io.openapiprocessor.core.logging.LoggerFactory
import io.openapiprocessor.core.model.HttpMethod
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.spring.model.parameters.MultipartParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter

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
            is MultipartParameter -> getMultipartAnnotation(parameter.contentType)
            else -> {
                log.error("unknown parameter type: ${parameter::class.java.name}")
                UNKNOWN_ANNOTATION
            }
        }
    }

    private fun getAnnotation(key: String): FrameworkAnnotation {
        return PARAMETER_ANNOTATIONS[key]!!
    }

    private fun getMultipartAnnotation(contentType: String?): FrameworkAnnotation {
        return if (contentType != null) {
            PARAMETER_ANNOTATIONS["multipart-part"]!!
        } else {
            PARAMETER_ANNOTATIONS["multipart-param"]!!
        }
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
    "multipart-param" to FrameworkAnnotation ("RequestParam", ANNOTATION_PKG),
    "multipart-part"  to FrameworkAnnotation ("RequestPart", ANNOTATION_PKG),
    "body"      to FrameworkAnnotation ("RequestBody", ANNOTATION_PKG)
)

private val UNKNOWN_ANNOTATION = FrameworkAnnotation("Unknown", "fix.me")
