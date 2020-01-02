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

package com.github.hauner.openapi.support

import spock.lang.Specification
import spock.lang.Unroll

class IdentifierSpec extends Specification {

    @Unroll
    void "convert source string '#src' to valid java identifier '#identifier'" () {
        expect:
        Identifier.toCamelCase (src) == identifier

        where:
        src              | identifier
        "a"              | "a"
        "a b"            | "aB"  // space is invalid
        "a-b"            | "aB"  // dash is invalid
        'api/some/thing' | 'apiSomeThing'  // slash is invalid
        "_ab"            | "ab"  // underscore is valid but unwanted
        "a_b"            | "aB"  // underscore is valid but unwanted
    }

    @Unroll
    void "convert source string '#src' to valid java enum identifier '#identifier'" () {
        expect:
        Identifier.toEnum (src) == identifier

        where:
        src             | identifier
        "a"              | "A"
        "a b"            | "A_B"  // space is invalid
        "a-b"            | "A_B"  // dash is invalid
        'api/some/thing' | 'API_SOME_THING'  // slash is invalid
        "_ab"            | "AB"   // underscore is valid but unwanted
        "a_b"            | "A_B"  // underscore is valid but unwanted
    }

    @Unroll
    void "converts source string '#src' to valid java class identifier '#identifier'" () {
        expect:
        Identifier.toClass (src) == identifier

        where:
        src              | identifier
        "a"              | "A"
        "a b"            | "AB"  // space is invalid
        "a-b"            | "AB"  // dash is invalid
        'api/some/thing' | 'ApiSomeThing'  // slash is invalid
        "_ab"            | "Ab"  // underscore is valid but unwanted
        "a_b"            | "AB"  // underscore is valid but unwanted
    }

}
