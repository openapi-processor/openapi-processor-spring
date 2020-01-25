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

package com.github.hauner.openapi.spring.generatr

import com.github.hauner.openapi.api.OpenApiGeneratr
import com.github.hauner.openapi.spring.converter.ApiConverter
import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.writer.ApiWriter
import com.github.hauner.openapi.spring.writer.BeanValidationFactory
import com.github.hauner.openapi.spring.writer.DataTypeWriter
import com.github.hauner.openapi.spring.writer.HeaderWriter
import com.github.hauner.openapi.spring.writer.InterfaceWriter
import com.github.hauner.openapi.spring.writer.MethodWriter
import com.github.hauner.openapi.spring.writer.StringEnumWriter
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 *  Entry point of openapi-generatr-spring.
 *
 *  @author Martin Hauner
 *  @author Bastian Wilhelm
 */
class SpringGeneratr implements OpenApiGeneratr {

    @Override
    String getName () {
        return 'spring'
    }

    @Override
    void run (Map<String, ?> generatrOptions) {
        ParseOptions opts = new ParseOptions(
            // loads $refs to other files into #/components/schema and replaces the $refs to the
            // external files with $refs to #/components/schema.
            resolve: true
        )

        SwaggerParseResult result = new OpenAPIV3Parser ()
            .readLocation (generatrOptions.apiPath as String, null, opts)

        if (generatrOptions.showWarnings) {
            printWarnings(result.messages)
        }

        def options = convertOptions (generatrOptions)
        def cv = new ApiConverter(options)
        def api = cv.convert (result.openAPI)

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
                apiOptions: options,
            ),
            new StringEnumWriter(headerWriter: headerWriter)
        )

        writer.write (api)
    }

    private ApiOptions convertOptions (Map<String, ?> generatrOptions) {
        def reader = new MappingReader ()
        def converter = new MappingConverter ()
        def mapping = reader.read (generatrOptions.typeMappings as String)

        def options = new ApiOptions ()
        options.apiPath = generatrOptions.apiPath
        options.targetDir = generatrOptions.targetDir

        if (generatrOptions.packageName) {
            options.packageName = generatrOptions.packageName
            println "warning: 'options:package-name' should be set in the mapping yaml!"
        }

        if (generatrOptions.containsKey ('beanValidation')) {
            options.beanValidation = generatrOptions.beanValidation
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

    private static printWarnings(List<String> warnings) {
        if (warnings.empty) {
            return
        }

        warnings.each {
            println it
        }
    }

}
