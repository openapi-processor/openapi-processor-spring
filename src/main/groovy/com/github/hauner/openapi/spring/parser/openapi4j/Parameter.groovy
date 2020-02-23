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

import com.github.hauner.openapi.spring.parser.Parameter as ParserParameter
import com.github.hauner.openapi.spring.parser.Schema as ParserSchema

/**
 * openapi4j Parameter abstraction.
 *
 * @author Martin Hauner
 */
class Parameter implements ParserParameter {

    @Override
    String getIn () {
        return null
    }

    @Override
    String getName () {
        return null
    }

    @Override
    ParserSchema getSchema () {
        return null
    }

    @Override
    Boolean isRequired () {
        return null
    }

}
