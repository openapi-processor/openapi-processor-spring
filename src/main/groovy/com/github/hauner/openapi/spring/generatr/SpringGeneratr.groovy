/*
 * Copyright 2019 the original authors
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
import com.github.hauner.openapi.spring.writer.ApiWriter
import com.github.hauner.openapi.spring.writer.HeaderWriter
import com.github.hauner.openapi.spring.writer.InterfaceWriter
import com.github.hauner.openapi.spring.writer.MethodWriter
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 *  Entry point of the openapi-generatr-gradle plugin.
 *
 *  @author Martin Hauner
 */
class SpringGeneratr implements OpenApiGeneratr<ApiOptions> {

    @Override
    String getName () {
        return 'spring'
    }

    @Override
    Class<ApiOptions> getOptionsType () {
        return ApiOptions
    }

    @Override
    void run (ApiOptions options) {
        ParseOptions opts = new ParseOptions(
            // loads $refs to other files into #/components/schema and replaces the $refs to the
            // external files with $refs to #/components/schema. The generatr can now always
            // generate the model based on #/components/schema.
            resolve: true
        )

        SwaggerParseResult result = new OpenAPIV3Parser ()
        .readLocation (options.apiPath, null, opts)

        if (options.showWarnings) {
            printWarnings(result.messages)
        }

        def cv = new ApiConverter(options)
        def api = cv.convert (result.openAPI)

        def writer = new ApiWriter (options,
            new InterfaceWriter(
                headerWriter: new HeaderWriter(),
                methodWriter: new MethodWriter())
        )

        writer.write (api)
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
