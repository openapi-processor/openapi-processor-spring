/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.core.parser.HttpMethod
import io.openapiprocessor.spring.model.parameters.MultipartParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * provides Spring annotation details.
 */
class SpringFrameworkAnnotations: FrameworkAnnotations {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getAnnotation(httpMethod: HttpMethod): Annotation {
        return MAPPING_ANNOTATIONS.getValue(httpMethod)
    }

    override fun getAnnotation(parameter: Parameter): Annotation {
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

    private fun getAnnotation(key: String): Annotation {
        return PARAMETER_ANNOTATIONS.getValue(key)
    }

    private fun getMultipartAnnotation(contentType: String?): Annotation {
        return if (contentType != null) {
            PARAMETER_ANNOTATIONS.getValue("multipart-part")
        } else {
            PARAMETER_ANNOTATIONS.getValue("multipart-param")
        }
    }
}

private val MAPPING_ANNOTATIONS = hashMapOf(
    HttpMethod.DELETE  to Annotation(getMappingAnnotationName(HttpMethod.DELETE.method)),
    HttpMethod.GET     to Annotation(getMappingAnnotationName(HttpMethod.GET.method)),
    HttpMethod.HEAD    to Annotation(getMappingAnnotationName("request"), linkedMapOf(
        "method" to SimpleParameterValue(
            "RequestMethod.HEAD", "org.springframework.web.bind.annotation.RequestMethod"
        ))
    ),
    HttpMethod.OPTIONS to Annotation(getMappingAnnotationName("request"), linkedMapOf(
        "method" to SimpleParameterValue(
            "RequestMethod.OPTIONS", "org.springframework.web.bind.annotation.RequestMethod"
        ))
    ),
    HttpMethod.PATCH   to Annotation(getMappingAnnotationName(HttpMethod.PATCH.method)),
    HttpMethod.POST    to Annotation(getMappingAnnotationName(HttpMethod.POST.method)),
    HttpMethod.PUT     to Annotation(getMappingAnnotationName(HttpMethod.PUT.method)),
    HttpMethod.TRACE   to Annotation(getMappingAnnotationName("request"), linkedMapOf(
        "method" to SimpleParameterValue(
            "RequestMethod.TRACE", "org.springframework.web.bind.annotation.RequestMethod"
        ))
    )
)

private val PARAMETER_ANNOTATIONS = hashMapOf(
    "query"           to Annotation (getAnnotationName("RequestParam")),
    "header"          to Annotation (getAnnotationName("RequestHeader")),
    "cookie"          to Annotation (getAnnotationName("CookieValue")),
    "path"            to Annotation (getAnnotationName("PathVariable")),
    "multipart-param" to Annotation (getAnnotationName("RequestParam")),
    "multipart-part"  to Annotation (getAnnotationName("RequestPart")),
    "body"            to Annotation (getAnnotationName("RequestBody"))
)

private val UNKNOWN_ANNOTATION = Annotation("Unknown")

private const val ANNOTATION_PKG = "org.springframework.web.bind.annotation"

private fun getMappingAnnotationName(mappingName: String): String {
    val mappingNameUpper = mappingName.replaceFirst(mappingName.first(), mappingName.first().uppercaseChar())
    return getAnnotationName("${mappingNameUpper}Mapping")
}

private fun getAnnotationName(name: String): String {
    return "${ANNOTATION_PKG}.${name}"
}
