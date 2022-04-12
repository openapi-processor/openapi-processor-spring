/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-core
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.api.OpenApiProcessor
import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.writer.java.*
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
            val javaDocWriter = JavaDocWriter()

            val writer = ApiWriter(
                options,
                InterfaceWriter(
                    options,
                    headerWriter,
                    MethodWriter(
                        options,
                        MappingAnnotationWriter(),
                        ParameterAnnotationWriter(annotations),
                        beanValidationFactory,
                        javaDocWriter
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
                InterfaceDataTypeWriter(
                    options,
                    headerWriter,
                    javaDocWriter
                )
            )

            writer.write (api)
        } catch (e: Exception) {
            log.error("processing failed!", e)
            throw e
        }
    }

    private fun convertOptions(processorOptions: Map<String, *>): ApiOptions {
        val options = OptionsConverter().convertOptions (processorOptions as Map<String, Any>)
        options.validate ()
        return options
    }

}
