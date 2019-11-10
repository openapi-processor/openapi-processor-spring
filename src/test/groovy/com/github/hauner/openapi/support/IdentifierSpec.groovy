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

package com.github.hauner.openapi.support

import spock.lang.Specification
import spock.lang.Unroll

class IdentifierSpec extends Specification {

    @Unroll
    void "convert json string '#json' to valid java identifier '#identifier'" () {
        expect:
        Identifier.fromJson (json) == identifier

        where:
        json  | identifier
        "a"   | "a"
        "a b" | "aB"  // space is invalid
        "a-b" | "aB"  // dash is invalid
        "_ab" | "ab"  // underscore is valid but unwanted
        "a_b" | "aB"  // underscore is valid but unwanted
    }

}
