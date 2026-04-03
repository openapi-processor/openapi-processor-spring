/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

class SpringAnnotations private constructor(val kind: String) {

    override fun toString(): String {
        return kind
    }

    companion object {
        val MAPPING = SpringAnnotations("mapping")
        val EXCHANGE = SpringAnnotations("exchange")

        val values: Array<SpringAnnotations> = arrayOf(MAPPING, EXCHANGE)

        fun values(): Array<SpringAnnotations> {
            return values.copyOf()
        }

        fun valueOf(kind: String?): SpringAnnotations {
            return when (kind?.lowercase()) {
                "exchange" -> EXCHANGE
                else -> MAPPING
            }
        }
    }
}
