/*
 * Copyright 2025 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.equals.shouldBeEqual
import io.openapiprocessor.core.model.Endpoint
import io.openapiprocessor.core.model.Response
import io.openapiprocessor.core.model.datatypes.NoneDataType
import io.openapiprocessor.core.openapi.HttpMethod
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations


class StatusAnnotationFactorySpec: StringSpec({
    val factory = StatusAnnotationFactory(SpringFrameworkAnnotations())

    "creates response status annotation" {
        val ep = Endpoint(
            "/foo",
            HttpMethod.GET,
            emptyList(),
            emptyList(),
            mapOf(
                "204" to listOf(Response("?", NoneDataType()))
            )
        )

        val codeSnippets = factory.create(ep, ep.endpointResponses.first())
        val responseStatus = codeSnippets.first()

        responseStatus.text shouldBeEqual """@ResponseStatus(HttpStatus.NO_CONTENT)"""
        responseStatus.imports shouldContainExactly setOf(
            "org.springframework.web.bind.annotation.ResponseStatus",
            "org.springframework.http.HttpStatus")
    }
})
