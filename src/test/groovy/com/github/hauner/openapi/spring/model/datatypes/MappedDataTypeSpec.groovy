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
