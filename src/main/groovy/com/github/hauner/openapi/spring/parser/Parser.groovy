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

package com.github.hauner.openapi.spring.parser

import com.github.hauner.openapi.spring.parser.swagger.Parser as Swagger
import com.github.hauner.openapi.spring.parser.openapi4j.Parser as OpenApi4J

/**
 * OpenAPI parser abstraction. Supports swagger or openapi4 parser.
 *
 * @author Martin Hauner
 */
class Parser {

    OpenApi parse (Map<String, ?> processorOptions) {
        def apiPath = processorOptions.apiPath as String

        switch (processorOptions.parser as ParserType) {

            case ParserType.SWAGGER:
                println "info: using SWAGGER parser"

                def parser = new Swagger ()
                return parser.parse (apiPath)

            case ParserType.OPENAPI4J:
                println "info: using OPENAPI4J parser"

                def parser = new OpenApi4J ()
                return parser.parse (apiPath)

            default:
                if (processorOptions.parser != null) {
                    println "warning: unknown parser type: ${processorOptions.parser}"
                    println "warning: available parsers: SWAGGER, OPENAPI4J"
                }

                def parser = new Swagger ()
                return parser.parse (apiPath)
        }

    }

}
