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

package com.github.hauner.openapi.spring.model.datatypes

import spock.lang.Specification
import spock.lang.Unroll

class MappedDataTypeSpec extends Specification {

    @Unroll
    void "get name of type with (optional) generic parameters" () {
        def type = new MappedDataType(pkg: 'model', type: 'Foo', genericTypes: generics)

        expect:
        type.name == typeName

        where:
        generics                                 | typeName
        []                                       | 'Foo'
        ['java.lang.String']                     | 'Foo<String>'
        ['java.lang.String', 'java.lang.String'] | 'Foo<String, String>'
    }

    @Unroll
    void "get imports of type with (optional) generic parameters" () {
        def type = new MappedDataType(pkg: 'model', type: 'Foo', genericTypes: generics)

        expect:
        type.imports == imports as Set

        where:
        generics                                 | imports
        []                                       | ['model.Foo']
        ['java.lang.String']                     | ['model.Foo', 'java.lang.String']
        ['java.lang.String', 'java.lang.String'] | ['model.Foo', 'java.lang.String']
    }

}
