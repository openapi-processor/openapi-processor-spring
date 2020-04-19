/*
 * Copyright 2020 the original authors
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

package com.github.hauner.openapi.spring.parser.swagger

import com.github.hauner.openapi.spring.parser.OpenApi as ParserOpenApi
import io.swagger.v3.parser.OpenAPIV3Parser
import io.swagger.v3.parser.core.models.ParseOptions
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 * swagger parser.
 *
 * @author Martin Hauner
 */
class Parser {

    ParserOpenApi parse (String apiPath) {
        if (!hasScheme (apiPath)) {
            apiPath = "file://${apiPath}"
        }

        ParseOptions opts = new ParseOptions(
            // loads $refs to other files into #/components/schema and replaces the $refs to the
            // external files with $refs to #/components/schema.
            resolve: true)

        SwaggerParseResult result = new OpenAPIV3Parser ()
                  .readLocation (apiPath, null, opts)

        new OpenApi(result)
    }

    boolean hasScheme (String path) {
        path.indexOf ("://") > -1
    }

}
