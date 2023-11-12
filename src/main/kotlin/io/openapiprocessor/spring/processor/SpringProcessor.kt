/*
 * Copyright 2019 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter
import io.openapiprocessor.core.parser.OpenApiParser
import io.openapiprocessor.core.writer.java.*
import io.openapiprocessor.spring.Version
import io.openapiprocessor.spring.writer.java.*
import io.openapiprocessor.spring.writer.java.MappingAnnotationWriter
import io.openapiprocessor.spring.writer.java.ParameterAnnotationWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.OffsetDateTime

/**
 *  openapi-processor-spring.
 */
class SpringProcessor {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)
    private var testMode = false

    fun run(processorOptions: MutableMap<String, *>) {
        try {
            val parser = OpenApiParser()
            val openapi = parser.parse(processorOptions)
            if (processorOptions["showWarnings"] != null) {
                openapi.printWarnings()
            }

            val framework = SpringFramework()
            val annotations = SpringFrameworkAnnotations()

            val options = convertOptions(processorOptions)
            val cv = ApiConverter(options, framework)
            val api = cv.convert(openapi)

            val writerFactory = SpringWriterFactory(options)
            val generatedInfo = createGeneratedInfo(options)
            val generatedWriter = GeneratedWriterImpl(generatedInfo, options)
            val beanValidations = BeanValidationFactory(getValidationFormat(options))
            val javaDocWriter = JavaDocWriter()

            val writer = ApiWriter(
                options,
                generatedWriter,
                InterfaceWriter(
                    options,
                    generatedWriter,
                    MethodWriter(
                        options,
                        MappingAnnotationWriter(annotations),
                        ParameterAnnotationWriter(annotations),
                        beanValidations,
                        javaDocWriter
                    ),
                    annotations,
                    beanValidations,
                    DefaultImportFilter()
                ),
                when (options.modelType) {
                    "record" -> DataTypeWriterRecord(
                        options,
                        generatedWriter,
                        beanValidations,
                        javaDocWriter
                    )
                    else -> DataTypeWriterPojo(
                        options,
                        generatedWriter,
                        beanValidations,
                        javaDocWriter
                    )
                },
                StringEnumWriter (generatedWriter),
                InterfaceDataTypeWriter(
                    options,
                    generatedWriter,
                    javaDocWriter
                ),
                GoogleFormatter(),
                writerFactory
            )

            writer.write (api)
        } catch (ex: Exception) {
            log.error("processing failed!", ex)
            throw ProcessingException(ex)
        }
    }

    private fun createGeneratedInfo(options: ApiOptions): GeneratedInfo {
        var version = Version.version
        var date: String? = OffsetDateTime.now().toString()

        if (!options.generatedDate)
            date = null

        if (testMode) {
            version = "test"
            date = null
        }

        return GeneratedInfo(
            "openapi-processor-spring",
            version,
            date
            //"https://openapiprocessor.io"
        )
    }

    fun enableTestMode () {
        testMode = true
    }

    @Suppress("UNCHECKED_CAST")
    private fun convertOptions(processorOptions: Map<String, *>): ApiOptions {
        val options = OptionsConverter().convertOptions (processorOptions as Map<String, Any>)
        options.validate ()
        return options
    }

    private fun getValidationFormat(options: ApiOptions): BeanValidationFormat {
        val format = options.beanValidationFormat
        return if (format != null)
            BeanValidationFormat.valueOf(format.uppercase())
        else
            BeanValidationFormat.JAVAX
    }
}
