/*
 * Copyright 2019-2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 *
 *  @author Martin Hauner
 *  @author Bastian Wilhelm
 */
class SpringProcessor: OpenApiProcessor {
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
        options.apiPath = processorOptions["apiPath"].toString()
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
            val packageName: String? = getPackageName(mapping)
            if (packageName != null) {
                options.packageName = packageName
            } else {
                log.warn("no 'options:package-name' set in mapping!")
            }

            val validation: Boolean? = getBeanValidation(mapping)
            if (validation != null) {
                options.beanValidation = validation
            }

            options.typeMappings = converter.convert(mapping)
        }

        return options
    }

    private fun getPackageName(mapping: MappingVersion): String? {
        return if (mapping is MappingV2) {
            mapping.options.packageName
        } else {
            (mapping as MappingV1).options.packageName
        }
    }

    private fun getBeanValidation(mapping: MappingVersion): Boolean? {
        return if (mapping is MappingV2) {
            mapping.options.beanValidation
        } else {
            (mapping as MappingV1).options.beanValidation
        }
    }

}
