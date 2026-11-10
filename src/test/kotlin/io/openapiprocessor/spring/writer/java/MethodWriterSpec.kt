/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.GenericDataType
import io.openapiprocessor.core.model.datatypes.MappedDataType
import io.openapiprocessor.core.openapi.HttpMethod
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.BeanValidationFormat
import io.openapiprocessor.core.writer.java.BeanValidations
import io.openapiprocessor.core.writer.java.JavaDocFactory
import io.openapiprocessor.core.writer.java.JavaIdentifier
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import java.io.StringWriter

class MethodWriterSpec: StringSpec({
    val apiOptions = ApiOptions()
    val identifier = JavaIdentifier()

    val writer = MethodWriter(
        apiOptions,
        identifier,
        StatusAnnotationFactory(SpringFrameworkAnnotations()),
        MappingAnnotationFactory(SpringFrameworkAnnotations()),
        ParameterAnnotationFactory(SpringFrameworkAnnotations()),
        BeanValidationFactory(apiOptions, BeanValidations.create(BeanValidationFormat.JAKARTA.name)),
        JavaDocFactory(identifier))

    "writes map from single query parameter" {
        val endpoint = endpoint("/foo", HttpMethod.GET) {
            responses {
                status("204") { empty() }
            }
            parameters {
                any(QueryParameter("foo", MappedDataType(
                    "Map",
                    "java.util",
                    listOf(
                        GenericDataType(DataTypeName("String", "String"), "java.lang"),
                        GenericDataType(DataTypeName("String", "String"), "java.lang")
                    )
                )))
            }
        }

        val target = StringWriter()
        writer.write(target, endpoint, endpoint.endpointResponses.first())

        target.toString() shouldBe """
            |    @ResponseStatus(HttpStatus.NO_CONTENT)
            |    @GetMapping(path = "${endpoint.path}")
            |    void getFoo(@RequestParam Map<String, String> foo);
            |""".trimMargin()
    }

    "head method" {
        val endpoint = endpoint("/foo", HttpMethod.HEAD) {
            responses {
                status("200") { empty() }
            }
            parameters {
                any(QueryParameter("foo", MappedDataType("String", "java.util")))
            }
        }

        val target = StringWriter()
        writer.write(target, endpoint, endpoint.endpointResponses.first())

        target.toString() shouldBe """
            |    @RequestMapping(path = "${endpoint.path}", method = RequestMethod.HEAD)
            |    void headFoo(@RequestParam(name = "foo", required = false) String foo);
            |""".trimMargin()
    }

    "trace method" {
        val endpoint = endpoint("/foo", HttpMethod.TRACE) {
            responses {
                status("200") { empty() }
            }
            parameters {
                any(QueryParameter("foo", MappedDataType("String", "java.util")))
            }
        }

        val target = StringWriter()
        writer.write(target, endpoint, endpoint.endpointResponses.first())

        target.toString() shouldBe """
            |    @RequestMapping(path = "${endpoint.path}", method = RequestMethod.TRACE)
            |    void traceFoo(@RequestParam(name = "foo", required = false) String foo);
            |""".trimMargin()
    }

    "option method" {
        val endpoint = endpoint("/foo", HttpMethod.OPTIONS) {
            responses {
                status("200") { empty() }
            }
            parameters {
                any(QueryParameter("foo", MappedDataType("String", "java.util")))
            }
        }

        val target = StringWriter()
        writer.write(target, endpoint, endpoint.endpointResponses.first())

        target.toString() shouldBe """
            |    @RequestMapping(path = "${endpoint.path}", method = RequestMethod.OPTIONS)
            |    void optionsFoo(@RequestParam(name = "foo", required = false) String foo);
            |""".trimMargin()
    }
})
