/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

@file:Suppress("DEPRECATION")

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.writer.DefaultWriterFactory

/**
 *  Entry point of openapi-processor-spring loaded via [java.util.ServiceLoader].
 */
class SpringService(private val testMode: Boolean = false):
    io.openapiprocessor.api.v1.OpenApiProcessor,
    io.openapiprocessor.api.OpenApiProcessor
{
    override fun getName(): String {
        return "spring"
    }

    override fun run(processorOptions: MutableMap<String, *>) {
        try {
            val processor = SpringProcessor(DefaultWriterFactory())
            if (testMode) {
                processor.enableTestMode()
            }
            processor.run(processorOptions)

        } catch (ex: Exception) {
            throw ex
        }
    }
}
