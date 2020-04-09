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

import com.github.hauner.openapi.api.OpenApiProcessor
import com.github.hauner.openapi.spring.converter.ApiConverter
import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.parser.OpenApi
import com.github.hauner.openapi.spring.parser.Parser
import com.github.hauner.openapi.spring.writer.ApiWriter
import com.github.hauner.openapi.spring.writer.BeanValidationFactory
import com.github.hauner.openapi.spring.writer.DataTypeWriter
import com.github.hauner.openapi.spring.writer.HeaderWriter
import com.github.hauner.openapi.spring.writer.InterfaceWriter
import com.github.hauner.openapi.spring.writer.MethodWriter
import com.github.hauner.openapi.spring.writer.StringEnumWriter
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
    
            def options = convertOptions (processorOptions)
            def cv = new ApiConverter(options)
            def api = cv.convert (openapi)
    
            def headerWriter = new HeaderWriter()
            def beanValidationFactory = new BeanValidationFactory()
    
            def writer = new ApiWriter (options,
                new InterfaceWriter(
                    headerWriter: headerWriter,
                    methodWriter: new MethodWriter(
                        beanValidationFactory: beanValidationFactory,
                        apiOptions: options
                    ),
                    beanValidationFactory: beanValidationFactory,
                    apiOptions: options
                ),
                new DataTypeWriter(
                    headerWriter: headerWriter,
                    beanValidationFactory: beanValidationFactory,
                    apiOptions: options
                ),
                new StringEnumWriter(headerWriter: headerWriter)
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
            }

            if (mapping?.options?.beanValidation != null) {
                options.beanValidation = mapping.options.beanValidation
            }

            options.typeMappings = converter.convert (mapping)
        }

        options
    }

}
