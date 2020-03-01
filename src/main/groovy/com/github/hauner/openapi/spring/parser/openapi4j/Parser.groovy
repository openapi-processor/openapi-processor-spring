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

package com.github.hauner.openapi.spring.parser.openapi4j

import com.github.hauner.openapi.spring.parser.OpenApi as ParserOpenApi
import org.openapi4j.core.validation.ValidationResults
import org.openapi4j.parser.OpenApi3Parser
import org.openapi4j.parser.model.v3.OpenApi3
import org.openapi4j.parser.validation.v3.OpenApi3Validator

/**
 * openapi4j parser.
 *
 * @author Martin Hauner
 */
class Parser {

    ParserOpenApi parse (String apiPath) {

        OpenApi3 api = new OpenApi3Parser ()
            .parse (new File (apiPath), false)

        ValidationResults results = null //OpenApi3Validator
//            .instance ()
//            .validate (api)

        new OpenApi (api, results)
    }

}
