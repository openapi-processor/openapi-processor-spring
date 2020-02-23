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
import com.github.hauner.openapi.spring.parser.Path as ParserPath
import com.github.hauner.openapi.spring.parser.RefResolver as ParserRefResolver
import io.swagger.v3.oas.models.PathItem as SwaggerPath
import io.swagger.v3.parser.core.models.SwaggerParseResult

/**
 * Swagger parser result.
 *
 * @author Martin Hauner
 */
class OpenApi implements ParserOpenApi {

    private SwaggerParseResult result

    OpenApi (SwaggerParseResult result) {
        this.result = result
    }

    @Override
    Map<String, ParserPath> getPaths () {
        Map<String, ParserPath> paths = new LinkedHashMap<> ()

        result.openAPI.paths.each { Map.Entry<String, SwaggerPath> pathEntry ->
            paths.put (pathEntry.key, new Path (pathEntry.key, pathEntry.value))
        }

        paths
    }

    @Override
    ParserRefResolver getRefResolver () {
        new RefResolver (result.openAPI.components)
    }

    @Override
    void printWarnings () {
        print (result.messages)
    }

    private static print (List<String> warnings) {
        if (warnings.empty) {
            return
        }

        warnings.each {
            println it
        }
    }

}
