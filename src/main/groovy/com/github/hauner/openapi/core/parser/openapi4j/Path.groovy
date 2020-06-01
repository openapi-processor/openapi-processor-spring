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

import com.github.hauner.openapi.core.model.HttpMethod
import com.github.hauner.openapi.core.parser.Operation as ParserOperation
import com.github.hauner.openapi.core.parser.Path as ParserPath
import org.openapi4j.parser.model.v3.Path as Oa4jPath

/**
 * openapi4j Path abstraction.
 *
 * @author Martin Hauner
 */
class Path implements ParserPath {

    String path
    private Oa4jPath info

    Path (String path, Oa4jPath info) {
        this.path = path
        this.info = info
    }

    @Override
    List<ParserOperation> getOperations () {
        def ops = []

        HttpMethod.values ().each {
            def op = info."${it.method}"
            if (op != null) {
                ops.add (new Operation(it, op))
            }
        }

        ops
    }

}
