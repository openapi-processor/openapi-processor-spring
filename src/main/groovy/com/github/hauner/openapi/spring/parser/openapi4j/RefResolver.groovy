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

import com.github.hauner.openapi.spring.parser.RefResolver as ParserRefResolver
import com.github.hauner.openapi.spring.parser.Schema as ParserSchema
import org.openapi4j.parser.model.v3.OpenApi3 as O4jOpenApi
import org.openapi4j.parser.model.v3.Schema as O4jSchema

/**
 * openapi4j $ref resolver.
 *
 * @author Martin Hauner
 */
class RefResolver implements ParserRefResolver {

    private O4jOpenApi api

    RefResolver (O4jOpenApi api) {
        this.api = api
    }

    @Override
    ParserSchema resolve (ParserSchema ref) {
        O4jSchema o4jSchema = (ref as Schema).schema
        def resolved = o4jSchema.copy (api.context, true)
        return new Schema (resolved)
    }

    private String getRefName (String ref) {
        ref.substring (ref.lastIndexOf ('/') + 1)
    }

}
