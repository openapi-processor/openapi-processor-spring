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

package com.github.hauner.openapi.core.support

import com.github.hauner.openapi.support.Identifier
import spock.lang.Specification
import spock.lang.Unroll

class IdentifierSpec extends Specification {

    @Unroll
    void "convert source string '#src' to valid identifiers: #identifier/#clazz/#enumn" () {
        expect:
        Identifier.toCamelCase (src) == identifier
        Identifier.toClass (src) == clazz
        Identifier.toEnum (src) == enumn

        where:
        src              | identifier     | clazz          | enumn

        // first char should be lowercase
        "a"              | "a"            | "A"            | "A"
        "A"              | "a"            | "A"            | "A"

        //
        "AA"             | "aa"           | "Aa"           | "AA"
        "AAFoo"          | "aaFoo"        | "AaFoo"        | "AA_FOO"

        // invalid chars are stripped
        "1a"             | "a"            | "A"            | "A"
        "2345a"          | "a"            | "A"            | "A"

        // word break at invalid characters
        "a foo"          | "aFoo"         | "AFoo"         | "A_FOO"
        "a-foo"          | "aFoo"         | "AFoo"         | "A_FOO"
        "a foo bar"      | "aFooBar"      | "AFooBar"      | "A_FOO_BAR"
        "a-foo-bar"      | "aFooBar"      | "AFooBar"      | "A_FOO_BAR"
        "a foo-bar"      | "aFooBar"      | "AFooBar"      | "A_FOO_BAR"
        'api/some/thing' | 'apiSomeThing' | "ApiSomeThing" | "API_SOME_THING"

        // word break at underscore, it is valid but unwanted
        "_ab"            | "ab"           | "Ab"           | "AB"
        "a_b"            | "aB"           | "AB"           | "A_B"
        "a_foo"          | "aFoo"         | "AFoo"         | "A_FOO"

        // final result is empty
        " "              | "invalid"      | "Invalid"      | "INVALID"
        "_"              | "invalid"      | "Invalid"      | "INVALID"
        "-"              | "invalid"      | "Invalid"      | "INVALID"

        // word break at uppercase
        "fooBar"         | "fooBar"       | "FooBar"       | "FOO_BAR"

        // upper case only at word break
        "fooBAr"         | "fooBar"       | "FooBar"       | "FOO_BAR"
        "fooBAR"         | "fooBar"       | "FooBar"       | "FOO_BAR"
        "FOO-bar"        | "fooBar"       | "FooBar"       | "FOO_BAR"
        "FOOBar"         | "fooBar"       | "FooBar"       | "FOO_BAR"
        "FOObar"         | "foObar"       | "FoObar"       | "FO_OBAR"
    }

}
