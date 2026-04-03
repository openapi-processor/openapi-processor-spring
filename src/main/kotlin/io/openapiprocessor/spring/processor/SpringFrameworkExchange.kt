/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.converter.mapping.SimpleParameterValue
import io.openapiprocessor.core.framework.FrameworkAnnotations
import io.openapiprocessor.core.model.Annotation
import io.openapiprocessor.core.model.EndpointResponseStatus
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.core.openapi.HttpMethod
import io.openapiprocessor.spring.model.parameters.MultipartParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * provides Spring exchange annotation details.
 */
class SpringFrameworkExchange: FrameworkAnnotations {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getAnnotation(httpMethod: HttpMethod): Annotation {
        if(EXCHANGE_ANNOTATIONS.containsKey(httpMethod)) {
            return EXCHANGE_ANNOTATIONS.getValue(httpMethod)
        }

        return Annotation(
            getServiceAnnotationName("Http"),
            linkedMapOf("method" to SimpleParameterValue(""""${httpMethod.toString().uppercase()}"""")))
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

    override fun getAnnotation(status: EndpointResponseStatus): Annotation {
        val statusCode = HTTP_STATUS[status.statusCode]
        if (statusCode == null) {
            log.error("unknown http status code: ${status.statusCode}")
            return UNKNOWN_ANNOTATION
        }

        return Annotation(
            "org.springframework.web.bind.annotation.ResponseStatus",
            linkedMapOf(
                "code" to SimpleParameterValue(statusCode, HTTP_STATUS_ENUM)))
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

private fun getBindAnnotationName(name: String): String =
    "org.springframework.web.bind.annotation.${name}"

private fun getServiceAnnotationName(methodName: String): String =
    "org.springframework.web.service.annotation.${methodName}Exchange"

private val EXCHANGE_ANNOTATIONS = hashMapOf(
    HttpMethod.DELETE  to Annotation(getServiceAnnotationName("Delete")),
    HttpMethod.GET     to Annotation(getServiceAnnotationName("Get")),
    HttpMethod.HEAD    to Annotation(getServiceAnnotationName("Http"),
        linkedMapOf("method" to SimpleParameterValue(""""HEAD""""))),
    HttpMethod.OPTIONS to Annotation(getServiceAnnotationName("Http"),
        linkedMapOf("method" to SimpleParameterValue(""""OPTIONS""""))),
    HttpMethod.PATCH   to Annotation(getServiceAnnotationName("Patch")),
    HttpMethod.POST    to Annotation(getServiceAnnotationName("Post")),
    HttpMethod.PUT     to Annotation(getServiceAnnotationName("Put")),
    HttpMethod.TRACE   to Annotation(getServiceAnnotationName("Http"),
        linkedMapOf("method" to SimpleParameterValue(""""TRACE"""")))
)

private val PARAMETER_ANNOTATIONS = hashMapOf(
    "query"           to Annotation (getBindAnnotationName("RequestParam")),
    "header"          to Annotation (getBindAnnotationName("RequestHeader")),
    "cookie"          to Annotation (getBindAnnotationName("CookieValue")),
    "path"            to Annotation (getBindAnnotationName("PathVariable")),
    "multipart-param" to Annotation (getBindAnnotationName("RequestParam")),
    "multipart-part"  to Annotation (getBindAnnotationName("RequestPart")),
    "body"            to Annotation (getBindAnnotationName("RequestBody"))
)

private val UNKNOWN_ANNOTATION = Annotation("Unknown")