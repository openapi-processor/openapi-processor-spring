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

package com.github.hauner.openapi.core.parser.openapi4j

import com.github.hauner.openapi.core.parser.MediaType as ParserMediaType
import com.github.hauner.openapi.core.parser.RequestBody as ParserRequestBody
import org.openapi4j.parser.model.v3.MediaType as O4jMediaType
import org.openapi4j.parser.model.v3.RequestBody as O4jRequestBody

/**
 * openapi4j RequestBody abstraction.
 *
 * @author Martin Hauner
 */
class RequestBody implements ParserRequestBody {

    private O4jRequestBody requestBody

    RequestBody (O4jRequestBody requestBody) {
        this.requestBody = requestBody
    }

    @Override
    Boolean getRequired () {
        requestBody.required
    }

    @Override
    Map<String, ParserMediaType> getContent () {
        def content = [:] as LinkedHashMap

        requestBody.contentMediaTypes.each { Map.Entry<String, O4jMediaType> entry ->
            content.put (entry.key, new MediaType (entry.value))
        }

        content
    }

}
