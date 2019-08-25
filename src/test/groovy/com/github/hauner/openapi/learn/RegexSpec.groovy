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

package com.github.hauner.openapi.learn

import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegexSpec extends Specification {

    void "regex group with java" () {
        Pattern p = Pattern.compile("has(.+?)Endpoints")
        Matcher m = p.matcher("hasPingEndpoints")

        expect:
        m.matches()
        "Ping" == m.group(1)
    }

    void "regex group with groovy ~ operator" () {
        Pattern p = ~/has(.+?)Endpoints/
        Matcher m = "hasPingEndpoints" =~ p

        expect:
        m instanceof Matcher
        m.find ()
        m.group (1) == "Ping"
    }

    void "match regex to boolean" () {
        expect:
        "has_XXXX_Endpoints" ==~ /has(.+?)Endpoints/
    }

    void "match line feeds with 'single line mode'"() {
        expect:
        "prefix\nXXXX\npostfix" ==~ /(?s)prefix(.+?)postfix/
    }
}
