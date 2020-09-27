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

package com.github.hauner.openapi.spring.processor

import com.github.hauner.openapi.spring.writer.java.ParameterAnnotationWriter
import io.openapiprocessor.api.OpenApiProcessor
import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.processor.MappingConverter
import io.openapiprocessor.core.processor.MappingReader
import io.openapiprocessor.core.parser.OpenApi
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.writer.java.ApiWriter
import io.openapiprocessor.core.writer.java.BeanValidationFactory
import io.openapiprocessor.core.writer.java.DataTypeWriter
import io.openapiprocessor.core.writer.java.DefaultImportFilter
import io.openapiprocessor.core.writer.java.InterfaceWriter
import io.openapiprocessor.core.writer.java.MethodWriter
import io.openapiprocessor.core.writer.java.StringEnumWriter
import io.openapiprocessor.spring.processor.SpringFramework
import io.openapiprocessor.spring.writer.java.HeaderWriter
import io.openapiprocessor.spring.writer.java.MappingAnnotationWriter
import org.slf4j.LoggerFactory

/**
 *  Entry point of openapi-processor-spring.
 *
 *  @author Martin Hauner
 *  @author Bastian Wilhelm
 */
class SpringProcessor implements OpenApiProcessor {
    private static final LOG = LoggerFactory.getLogger (SpringProcessor)

    @Override
    String getName () {
        return 'spring'
    }

    @Override
    void run (Map<String, ?> processorOptions) {
        try {
            def parser = new Parser ()
            OpenApi openapi = parser.parse (processorOptions)
            if (processorOptions.showWarnings) {
                openapi.printWarnings ()
            }

            def framework = new SpringFramework()
            def annotations = new SpringFrameworkAnnotations()

            def options = convertOptions (processorOptions)
            def cv = new ApiConverter(options, framework)
            def api = cv.convert (openapi)

            def headerWriter = new HeaderWriter()
            def beanValidationFactory = new BeanValidationFactory()

            def writer = new ApiWriter(
                options,
                new InterfaceWriter(
                    options,
                    headerWriter,
                    new MethodWriter(
                        options,
                        new MappingAnnotationWriter (),
                        new ParameterAnnotationWriter (annotations: annotations),
                        beanValidationFactory
                    ),
                    annotations,
                    beanValidationFactory,
                    new DefaultImportFilter ()
                ),
                new DataTypeWriter(
                    options,
                    headerWriter,
                    beanValidationFactory),
                new StringEnumWriter(headerWriter),
                true
            )

            writer.write (api)
        } catch (Exception e) {
            LOG.error ("processing failed!", e)
            throw e
        }
    }

    private ApiOptions convertOptions (Map<String, ?> processorOptions) {
        def reader = new MappingReader ()
        def converter = new MappingConverter ()
        def mapping

        if (processorOptions.containsKey ('mapping')) {
            mapping = reader.read (processorOptions.mapping as String)

        } else if (processorOptions.containsKey ('typeMappings')) {
            mapping = reader.read (processorOptions.typeMappings as String)
            println "warning: 'typeMappings' option is deprecated, use 'mapping'!"
        }

        def options = new ApiOptions ()
        options.apiPath = processorOptions.apiPath
        options.targetDir = processorOptions.targetDir

        if (processorOptions.packageName) {
            options.packageName = processorOptions.packageName
            println "warning: 'options:package-name' should be set in the mapping yaml!"
        }

        if (processorOptions.containsKey ('beanValidation')) {
            options.beanValidation = processorOptions.beanValidation
            println "warning: 'options:bean-validation' should be set in the mapping yaml!"
        }

        if (mapping) {
            if (mapping?.options?.packageName != null) {
                options.packageName = mapping.options.packageName
            } else {
                LOG.warn ("no 'options:package-name' set in mapping!")
            }

            if (mapping?.options?.beanValidation != null) {
                options.beanValidation = mapping.options.beanValidation
            }

            options.typeMappings = converter.convert (mapping)
        }

        options
    }

}
