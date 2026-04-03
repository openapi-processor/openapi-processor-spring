/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.kotest.core.spec.style.StringSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.openapiprocessor.core.builder.api.endpoint
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.openapi.HttpMethod
import io.openapiprocessor.spring.processor.SpringFrameworkExchange

class ExchangeAnnotationFactorySpec: StringSpec({
    val factory = ExchangeAnnotationFactory(SpringFrameworkExchange())

    data class TestHttpMethod(val httpMethod: HttpMethod, val path: String, val expected: String)

    withData(
        nameFn = {"writes http method specific exchange annotation: ${it.httpMethod}"},

        TestHttpMethod(HttpMethod.GET, "get-it", """@GetExchange(url = "get-it")"""),
        TestHttpMethod(HttpMethod.PUT, "put-it", """@PutExchange(url = "put-it")"""),
        TestHttpMethod(HttpMethod.POST, "post-it", """@PostExchange(url = "post-it")"""),
        TestHttpMethod(HttpMethod.DELETE, "delete-it", """@DeleteExchange(url = "delete-it")"""),
        TestHttpMethod(HttpMethod.OPTIONS, "options-it", """@HttpExchange(url = "options-it", method = "OPTIONS")"""),
        TestHttpMethod(HttpMethod.HEAD, "head-it", """@HttpExchange(url = "head-it", method = "HEAD")"""),
        TestHttpMethod(HttpMethod.PATCH, "patch-it", """@PatchExchange(url = "patch-it")"""),
        TestHttpMethod(HttpMethod.TRACE, "trace-it", """@HttpExchange(url = "trace-it", method = "TRACE")"""),
        TestHttpMethod(HttpMethod.valueOf("CUSTOM"), "custom-it", """@HttpExchange(url = "custom-it", method = "CUSTOM")""")
    )
    { (httpMethod, path, expected) ->
        val endpoint = endpoint(path = path, method = httpMethod) {
            responses {
                status("204") { empty() }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldBe expected
    }

    data class TestContentType(val contentType: String, val expected: String)

    withData(
        nameFn = {"writes 'contentType' parameter with body content type ${it.contentType}"},

        TestContentType("plain/text", """@GetExchange(url = "/foo", contentType = "plain/text")"""),
        TestContentType("application/json", """@GetExchange(url = "/foo", contentType = "application/json")"""),
    )
    { (contentType, expected) ->
        val endpoint = endpoint(path = "/foo", method = HttpMethod.GET) {
            parameters {
                body(name = "body", contentType = contentType, StringDataType())
            }
            responses {
                status("204") { empty() }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldBe expected
    }

    data class TestAccept(val accept: String, val expected: String)

    withData(
        nameFn = {"writes 'accept' parameter with response content type ${it.accept}"},

        TestAccept("plain/text", """@GetExchange(url = "/foo", accept = {"plain/text"})"""),
        TestAccept("application/json", """@GetExchange(url = "/foo", accept = {"application/json"})"""),
    )
    { (accept, expected) ->
        val endpoint = endpoint(path = "/foo", method = HttpMethod.GET) {
            responses {
                status("204") {
                    response(contentType = accept, StringDataType())
                }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldBe expected
    }

    data class TestAcceptContentType(val accept: String, val contentType: String, val expected: String)

    withData(
        nameFn = {"writes 'accept' & 'contentType' parameters ${it.accept}/${it.contentType}"},

        TestAcceptContentType("application/json", "plain/text","""@GetExchange(url = "/foo", contentType = "plain/text", accept = {"application/json"})"""),
        TestAcceptContentType("plain/text", "application/json","""@GetExchange(url = "/foo", contentType = "application/json", accept = {"plain/text"})""")
    )
    { (accept, contentType, expected) ->
        val endpoint = endpoint(path = "/foo", method = HttpMethod.GET) {
            parameters {
                body(name = "body", contentType = contentType, StringDataType())
            }
            responses {
                status("200") {
                    response(contentType = accept, StringDataType())
                }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldBe expected
    }

    "writes exchange annotation with multiple result content types" {
        val endpoint = endpoint(path = "/foo", method = HttpMethod.GET) {
            responses {
                status("200") {
                    response("application/json", StringDataType())
                }
                status("default") {
                    response("text/plain", StringDataType())
                }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldBe  """@GetExchange(url = "${endpoint.path}", accept = {"${endpoint.responses["200"]?.first ()?.contentType}", "${endpoint.responses["default"]?.first ()?.contentType}"})"""
    }

    "writes unique 'contentType' parameter" {
        val endpoint = endpoint(path = "/foo", method = HttpMethod.GET) {
            parameters {
                body("body", "application/json", StringDataType())
                body("body", "application/json", StringDataType())
                body("body", "application/json", StringDataType())
            }
            responses {
                status("204") { empty() }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldContain """contentType = "application/json""""
    }

    "writes unique 'accept' parameter" {
        val endpoint = endpoint(path = "/foo", method = HttpMethod.GET) {
            responses {
                status("200") {
                    response("application/json", StringDataType())
                }
                status("400") {
                    response("application/json", StringDataType())
                }
                status("401") {
                    response("application/json", StringDataType())
                }
                status("403") {
                    response("application/json", StringDataType())
                }
            }
        }

        val annotations = factory.create(endpoint, endpoint.endpointResponses.first())

        annotations.first() shouldContain """accept = {"application/json"}"""
    }
})
