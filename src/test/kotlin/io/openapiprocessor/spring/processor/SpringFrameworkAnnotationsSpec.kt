/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.EndpointResponseStatus
import io.openapiprocessor.core.model.HttpStatus


class SpringFrameworkAnnotationsSpec : StringSpec({

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
