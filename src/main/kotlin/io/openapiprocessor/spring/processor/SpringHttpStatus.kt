/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

// To avoid a dependency on Spring, the map provides the http status enum names.
// This may break if the enum name does not exist in the used Spring version.

// https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml

val HTTP_STATUS = hashMapOf(
    "100" to getEnum("CONTINUE"),
    "101" to getEnum("SWITCHING_PROTOCOLS"),
    "102" to getEnum("PROCESSING"), // WebDAV
    "103" to getEnum("CHECKPOINT"),

    "200" to getEnum("OK"),
    "201" to getEnum("CREATED"),
    "202" to getEnum("ACCEPTED"),
    "203" to getEnum("NON_AUTHORITATIVE_INFORMATION"),
    "204" to getEnum("NO_CONTENT"),
    "205" to getEnum("RESET_CONTENT"),
    "206" to getEnum("PARTIAL_CONTENT"),
    "207" to getEnum("MULTI_STATUS"), // WebDAV
    "208" to getEnum("ALREADY_REPORTED"), // WebDAV
    "226" to getEnum("IM_USED"),

    "300" to getEnum("MULTIPLE_CHOICES"),
    "301" to getEnum("MOVED_PERMANENTLY"),
    "302" to getEnum("FOUND"), // was MOVED_TEMPORARILY
    "303" to getEnum("SEE_OTHER"),
    "304" to getEnum("NOT_MODIFIED"),
    "305" to getEnum("USE_PROXY"),
    "307" to getEnum("TEMPORARY_REDIRECT"),
    "308" to getEnum("PERMANENT_REDIRECT"),

    "400" to getEnum("BAD_REQUEST"),
    "401" to getEnum("UNAUTHORIZED"),
    "402" to getEnum("PAYMENT_REQUIRED"),
    "403" to getEnum("FORBIDDEN"),
    "404" to getEnum("NOT_FOUND"),
    "405" to getEnum("METHOD_NOT_ALLOWED"),
    "406" to getEnum("NOT_ACCEPTABLE"),
    "407" to getEnum("PROXY_AUTHENTICATION_REQUIRED"),
    "408" to getEnum("REQUEST_TIMEOUT"),
    "409" to getEnum("CONFLICT"),
    "410" to getEnum("GONE"),
    "411" to getEnum("LENGTH_REQUIRED"),
    "412" to getEnum("PRECONDITION_FAILED"),
    "413" to getEnum("PAYLOAD_TOO_LARGE"), // was REQUEST_ENTITY_TOO_LARGE
    "414" to getEnum("URI_TOO_LONG"), // was REQUEST_URI_TOO_LONG
    "415" to getEnum("UNSUPPORTED_MEDIA_TYPE"),
    "416" to getEnum("REQUESTED_RANGE_NOT_SATISFIABLE"),
    "417" to getEnum("EXPECTATION_FAILED"),
    "418" to getEnum("I_AM_A_TEAPOT"),
    "419" to getEnum("INSUFFICIENT_SPACE_ON_RESOURCE"), // WebDAV
    "420" to getEnum("METHOD_FAILURE"), // WebDAV
    "421" to getEnum("DESTINATION_LOCKED"), // WebDAV
    "422" to getEnum("UNPROCESSABLE_ENTITY"), // WebDAV
    "423" to getEnum("LOCKED"), // WebDAV
    "424" to getEnum("FAILED_DEPENDENCY"), // WebDAV
    "425" to getEnum("TOO_EARLY"),
    "426" to getEnum("UPGRADE_REQUIRED"),
    "428" to getEnum("PRECONDITION_REQUIRED"),
    "429" to getEnum("TOO_MANY_REQUESTS"),
    "431" to getEnum("REQUEST_HEADER_FIELDS_TOO_LARGE"),
    "451" to getEnum("UNAVAILABLE_FOR_LEGAL_REASONS"),

    "500" to getEnum("INTERNAL_SERVER_ERROR"),
    "501" to getEnum("NOT_IMPLEMENTED"),
    "502" to getEnum("BAD_GATEWAY"),
    "503" to getEnum("SERVICE_UNAVAILABLE"),
    "504" to getEnum("GATEWAY_TIMEOUT"),
    "505" to getEnum("HTTP_VERSION_NOT_SUPPORTED"),
    "506" to getEnum("VARIANT_ALSO_NEGOTIATES"),
    "507" to getEnum("INSUFFICIENT_STORAGE"), // WebDAV
    "508" to getEnum("LOOP_DETECTED"), // WebDAV
    "509" to getEnum("BANDWIDTH_LIMIT_EXCEEDED"),
    "510" to getEnum("NOT_EXTENDED"),
    "511" to getEnum("NETWORK_AUTHENTICATION_REQUIRED")
)

const val HTTP_STATUS_ENUM = "org.springframework.http.HttpStatus"

fun getEnum(name: String): String {
    return "HttpStatus.${name}"
}
