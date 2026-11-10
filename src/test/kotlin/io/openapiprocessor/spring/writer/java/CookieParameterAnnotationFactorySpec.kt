/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations

class CookieParameterAnnotationFactorySpec: StringSpec({
    val factory = ParameterAnnotationFactory(SpringFrameworkAnnotations())

    "create simple (required) cookie parameter" {
        val param = CookieParameter("foo", StringDataType(), true)

        val codeSnippets = factory.create(param)

        val cookie = codeSnippets.first()
        cookie.text shouldBe """@CookieValue(name = "foo")"""
        cookie.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.CookieValue")
    }

    "create simple (optional) cookie parameter" {
        val param = CookieParameter("foo", StringDataType(), false)

        val codeSnippets = factory.create(param)

        val cookie = codeSnippets.first()
        cookie.text shouldBe """@CookieValue(name = "foo", required = false)"""
        cookie.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.CookieValue")
    }

    "create simple (optional with default) cookie parameter" {
        val param = CookieParameter(
            "foo",
            StringDataType("string", DataTypeConstraints("bar")),
            false)

        val codeSnippets = factory.create(param)

        val cookie = codeSnippets.first()
        cookie.text shouldBe """@CookieValue(name = "foo", required = false, defaultValue = "bar")"""
        cookie.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.CookieValue")
    }
})
