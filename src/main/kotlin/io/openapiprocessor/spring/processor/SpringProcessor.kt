/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.api.OpenApiProcessor
import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.processor.mapping.MappingVersion
import io.openapiprocessor.core.processor.mapping.v1.Mapping as MappingV1
import io.openapiprocessor.core.processor.mapping.v2.Mapping as MappingV2
import io.openapiprocessor.core.writer.java.ApiWriter
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DataTypeWriter
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.InterfaceWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.StringEnumWriter
import io.openapiprocessor.spring.writer.java.HeaderWriter
import io.openapiprocessor.spring.writer.java.MappingAnnotationWriter
import io.openapiprocessor.spring.writer.java.ParameterAnnotationWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *  Entry point of openapi-processor-spring.
 */
class SpringProcessor: OpenApiProcessor, io.openapiprocessor.api.v1.OpenApiProcessor {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    override fun getName(): String {
        return "spring"
    }

    override fun run(processorOptions: MutableMap<String, *>) {
        try {
            val parser = Parser()
            val openapi = parser.parse(processorOptions)
            if (processorOptions["showWarnings"] != null) {
                openapi.printWarnings()
            }

            val framework = SpringFramework()
            val annotations = SpringFrameworkAnnotations()

            val options = convertOptions(processorOptions)
            val cv = ApiConverter(options, framework)
            val api = cv.convert(openapi)

            val headerWriter = HeaderWriter()
            val beanValidationFactory = BeanValidationFactory()

            val writer = ApiWriter(
                options,
                InterfaceWriter(
                    options,
                    headerWriter,
                    MethodWriter(
                        options,
                        MappingAnnotationWriter(),
                        ParameterAnnotationWriter(annotations),
                        beanValidationFactory
                    ),
                    annotations,
                    beanValidationFactory,
                    DefaultImportFilter()
                ),
                DataTypeWriter(
                    options,
                    headerWriter,
                    beanValidationFactory),
                StringEnumWriter (headerWriter),
                true
            )

            writer.write (api)
        } catch (e: Exception) {
            log.error("processing failed!", e)
            throw e
        }
    }

    private fun convertOptions(processorOptions: Map<String, *>): ApiOptions {
        val reader = MappingReader()
        val converter = MappingConverter()
        var mapping: MappingVersion? = null

        if (processorOptions.containsKey("mapping")) {
            mapping = reader.read(processorOptions["mapping"].toString())

        } else if (processorOptions.containsKey("typeMappings")) {
            mapping = reader.read(processorOptions["typeMappings"].toString())
            log.warn("'typeMappings' option is deprecated, use 'mapping'!")
        }

        val options = ApiOptions()
        options.targetDir = processorOptions["targetDir"].toString()

        if (processorOptions.containsKey("packageName")) {
            options.packageName = processorOptions["packageName"].toString()
            log.warn("'options:package-name' should be set in the mapping yaml!")
        }

        if (processorOptions.containsKey("beanValidation")) {
            options.beanValidation = processorOptions["beanValidation"] as Boolean
            log.warn("options:bean-validation' should be set in the mapping yaml!")
        }

        if (mapping != null) {
            if (mapping is MappingV1) {
                options.packageName = mapping.options.packageName
                options.beanValidation = mapping.options.beanValidation

            } else if (mapping is MappingV2) {
                options.packageName = mapping.options.packageName
                options.beanValidation = mapping.options.beanValidation
                options.javadoc = mapping.options.javadoc
            }

            if (options.packageName.contains("io.openapiprocessor.")) {
                log.warn("is 'options:package-name' set in mapping? found: '{}'.", options.packageName)
            }

            options.typeMappings = converter.convert(mapping)
        } else {
            log.warn("missing 'mapping.yaml' configuration!")
        }

        return options
    }

}
