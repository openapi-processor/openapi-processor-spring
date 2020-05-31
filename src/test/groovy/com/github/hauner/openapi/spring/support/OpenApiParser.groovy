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

package com.github.hauner.openapi.spring.support

import com.github.hauner.openapi.core.parser.OpenApi
import com.github.hauner.openapi.core.parser.ParserType
import com.github.hauner.openapi.spring.support.parser.OpenApi4jParser
import com.github.hauner.openapi.spring.support.parser.SwaggerParser

/**
 * OpenAPI parser to read yaml from memory (swagger or openapi4j).
 */
class OpenApiParser {

    static OpenApi parse (String apiYaml, ParserType parserType = ParserType.SWAGGER) {
        switch (parserType) {
            case ParserType.SWAGGER:
                def parser = new SwaggerParser ()
                return parser.parseYaml (apiYaml)

            case ParserType.OPENAPI4J:
                def parser = new OpenApi4jParser ()
                return parser.parseYaml (apiYaml)
        }
    }

    private static printWarnings(List<String> warnings) {
        if (warnings.empty) {
            return
        }

        println "OpenAPI warnings:"
        warnings.each {
            println it
        }
    }

}
