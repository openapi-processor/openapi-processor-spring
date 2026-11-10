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
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations

class PathParameterAnnotationFactorySpec: StringSpec({
    val factory = ParameterAnnotationFactory(SpringFrameworkAnnotations())

    "creates simple (required) path parameter" {
        val param = PathParameter("foo", StringDataType(), true)

        val codeSnippets = factory.create(param)

        val path = codeSnippets.first()
        path.text shouldBe """@PathVariable(name = "foo")"""
        path.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.PathVariable")
    }

    "creates simple (optional) path parameter" {
        val param = PathParameter("foo", StringDataType(), false)

        val codeSnippets = factory.create(param)

        val path = codeSnippets.first()
        path.text shouldBe """@PathVariable(name = "foo", required = false)"""
        path.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.PathVariable")
    }

    "creates simple (optional with default) path parameter" {
        val param = PathParameter(
            "foo",
            StringDataType("string", DataTypeConstraints("bar")),
            false)

        val codeSnippets = factory.create(param)

        val path = codeSnippets.first()
        path.text shouldBe """@PathVariable(name = "foo", required = false, defaultValue = "bar")"""
        path.imports shouldContainExactly setOf("org.springframework.web.bind.annotation.PathVariable")
    }
})
