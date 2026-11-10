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
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations

class HeaderParameterAnnotationFactorySpec: StringSpec({
    val factory = ParameterAnnotationFactory(SpringFrameworkAnnotations())

    "create simple (required) header parameter" {
        val param = HeaderParameter("foo", StringDataType(), true)

        val codeSnippets = factory.create(param)

        val header = codeSnippets.first()
        header.text shouldBe """@RequestHeader(name = "foo")"""
        header.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.RequestHeader")
    }

    "create simple (optional) header parameter" {
        val param = HeaderParameter("foo", StringDataType(), false)

        val codeSnippets = factory.create(param)

        val header = codeSnippets.first()
        header.text shouldBe """@RequestHeader(name = "foo", required = false)"""
        header.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.RequestHeader")
    }

    "create simple (optional with default) header parameter" {
        val param = HeaderParameter(
            "foo",
            StringDataType("string", DataTypeConstraints("bar")),
            false)

        val codeSnippets = factory.create(param)

        val header = codeSnippets.first()
        header.text shouldBe """@RequestHeader(name = "foo", required = false, defaultValue = "bar")"""
        header.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.RequestHeader")
    }
})
