/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.EndpointResponseStatus
import io.openapiprocessor.core.model.HttpStatus
import io.openapiprocessor.core.openapi.HttpMethod


class SpringFrameworkExchangeSpec : StringSpec({

    "provides known HTTP method annotations" {
        val annotations = SpringFrameworkExchange()

        val get = annotations.getAnnotation(HttpMethod.GET)
        get.annotationName shouldBe "@GetExchange"
        get.typeName shouldBe "GetExchange"
        get.packageName shouldBe "org.springframework.web.service.annotation."
        get.imports shouldContainExactly setOf("org.springframework.web.service.annotation.GetExchange")

        val del = annotations.getAnnotation(HttpMethod.DELETE)
        del.annotationName shouldBe "@DeleteExchange"
        del.typeName shouldBe "DeleteExchange"
        del.packageName shouldBe "org.springframework.web.service.annotation."
        del.imports shouldContainExactly setOf("org.springframework.web.service.annotation.DeleteExchange")

        val head = annotations.getAnnotation(HttpMethod.HEAD)
        head.annotationName shouldBe "@HttpExchange"
        head.typeName shouldBe "HttpExchange"
        head.packageName shouldBe "org.springframework.web.service.annotation."
        head.imports shouldContainExactly setOf("org.springframework.web.service.annotation.HttpExchange")
        head.parameters["method"]!!.value shouldBe """"HEAD""""

        val options = annotations.getAnnotation(HttpMethod.OPTIONS)
        options.annotationName shouldBe "@HttpExchange"
        options.typeName shouldBe "HttpExchange"
        options.packageName shouldBe "org.springframework.web.service.annotation."
        options.imports shouldContainExactly setOf("org.springframework.web.service.annotation.HttpExchange")
        options.parameters["method"]!!.value shouldBe """"OPTIONS""""

        val patch = annotations.getAnnotation(HttpMethod.PATCH)
        patch.annotationName shouldBe "@PatchExchange"
        patch.typeName shouldBe "PatchExchange"
        patch.packageName shouldBe "org.springframework.web.service.annotation."
        patch.imports shouldContainExactly setOf("org.springframework.web.service.annotation.PatchExchange")

        val post = annotations.getAnnotation(HttpMethod.POST)
        post.annotationName shouldBe "@PostExchange"
        post.typeName shouldBe "PostExchange"
        post.packageName shouldBe "org.springframework.web.service.annotation."
        post.imports shouldContainExactly setOf("org.springframework.web.service.annotation.PostExchange")

        val put = annotations.getAnnotation(HttpMethod.PUT)
        put.annotationName shouldBe "@PutExchange"
        put.typeName shouldBe "PutExchange"
        put.packageName shouldBe "org.springframework.web.service.annotation."
        put.imports shouldContainExactly setOf("org.springframework.web.service.annotation.PutExchange")

        val trace = annotations.getAnnotation(HttpMethod.TRACE)
        trace.annotationName shouldBe "@HttpExchange"
        trace.typeName shouldBe "HttpExchange"
        trace.packageName shouldBe "org.springframework.web.service.annotation."
        trace.imports shouldContainExactly setOf("org.springframework.web.service.annotation.HttpExchange")
        trace.parameters["method"]!!.value shouldBe """"TRACE""""

        val custom = annotations.getAnnotation(HttpMethod.valueOf("CUSTOM"))
        custom.annotationName shouldBe "@HttpExchange"
        custom.typeName shouldBe "HttpExchange"
        custom.packageName shouldBe "org.springframework.web.service.annotation."
        custom.imports shouldContainExactly setOf("org.springframework.web.service.annotation.HttpExchange")
        custom.parameters["method"]!!.value shouldBe """"CUSTOM""""
    }

    class Status(override val statusCode: HttpStatus) : EndpointResponseStatus

    "provides known HttpStatus annotation with specific status code import" {
        val framework = SpringFrameworkAnnotations()

        val annotation200 = framework.getAnnotation(Status("200"))
        annotation200.referencedImports shouldContainExactly setOf("org.springframework.http.HttpStatus")
        annotation200.parameters["code"]!!.value shouldBe "HttpStatus.OK"

        val annotation400 = framework.getAnnotation(Status("400"))
        annotation400.referencedImports shouldContainExactly setOf("org.springframework.http.HttpStatus")
        annotation400.parameters["code"]!!.value shouldBe "HttpStatus.BAD_REQUEST"

        val annotation500 = framework.getAnnotation(Status("500"))
        annotation500.referencedImports shouldContainExactly setOf("org.springframework.http.HttpStatus")
        annotation500.parameters["code"]!!.value shouldBe "HttpStatus.INTERNAL_SERVER_ERROR"
    }
})
