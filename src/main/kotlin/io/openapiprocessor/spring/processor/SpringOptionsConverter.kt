/*
 * Copyright 2026 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.converter.InvalidMappingException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SpringOptionsConverter(private val mappingReader: SpringMappingReader) {
    var log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    fun fillOptions(processorOptions: Map<String, Any>, options: SpringApiOptions) {
        if (processorOptions.containsKey("mapping")) {
            readMapping(processorOptions["mapping"].toString(), options)
        } else {
            log.warn("required option 'mapping' is missing!")
        }
    }

    private fun readMapping(mappingSource: String, options: SpringApiOptions) {
        try {
            val mapping = mappingReader.read(mappingSource)
            if (mapping == null) {
                log.warn("missing 'mapping.yaml' configuration!")
                return
            }

            options.springAnnotations = mapping.options.spring.annotations

        } catch (t: Throwable) {
            throw InvalidMappingException("failed to parse 'mapping.yaml' configuration!", t)
        }
    }
}
