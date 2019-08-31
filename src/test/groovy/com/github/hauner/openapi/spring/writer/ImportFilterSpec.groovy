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

package com.github.hauner.openapi.spring.writer

import spock.lang.Specification

class ImportFilterSpec extends Specification {
    def filter = new ImportFilter()

    void "drops imports from same package" () {
        when:
        def result = filter.filter ('same', [
            'other.Foo',
            'same.Bar'
        ] as Set)

        then:
        result.size () == 1
        result.first () == 'other.Foo'
    }

    void "drops java.lang imports" () {
        when:
        def result = filter.filter ('any', [
            'java.lang.String',
            'java.lang.Long',
            'other.Foo'
        ] as Set)

        then:
        result.size () == 1
        result.first () == 'other.Foo'
    }

    void "provides empty list when no imports are left" () {
        when:
        def result = filter.filter ('any', [
            'java.lang.String',
        ] as Set)

        then:
        result.empty
    }

    void "corrects imports of generic types" () {
        when:
        def result = filter.filter ('any', [
            'java.util.Collection<String>'
        ] as Set)

        then:
        result.size () == 1
        result.first () == 'java.util.Collection'
    }
}
