/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.model.RequestBody
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.LongDataType
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import io.openapiprocessor.core.model.parameters.HeaderParameter
import io.openapiprocessor.core.model.parameters.PathParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import spock.lang.Specification
import spock.lang.Unroll

class ParameterAnnotationFactorySpec extends Specification {
    def factory = new ParameterAnnotationFactory(new SpringFrameworkAnnotations())

    @Unroll
    void "creates simple (optional) query parameter with quoted string default value" () {
        def param = clazz.newInstance('foo',
            new StringDataType(
                'string',
                createConstraints ('bar'), false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def parameter = codeSnippets.first()
        parameter.text == """${annotation}(name = "foo", required = false, defaultValue = "bar")"""
        parameter.imports.contains("org.springframework.web.bind.annotation.${annotation.substring(1)}".toString())

        where:
        clazz           | annotation
        QueryParameter  | "@RequestParam"
        PathParameter   | "@PathVariable"
        CookieParameter | "@CookieValue"
        HeaderParameter | "@RequestHeader"

        // todo imports
    }

    void "creates simple (optional) query parameter with quoted number default value" () {
        def param = clazz.newInstance ('foo',
            new LongDataType (
                'integer:int64',
                createConstraints (5), false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def parameter = codeSnippets.first()
        parameter.text == """${annotation}(name = "foo", required = false, defaultValue = "5")"""
        parameter.imports.contains("org.springframework.web.bind.annotation.${annotation.substring(1)}".toString())

        where:
        clazz           | annotation
        QueryParameter  | "@RequestParam"
        PathParameter   | "@PathVariable"
        CookieParameter | "@CookieValue"
        HeaderParameter | "@RequestHeader"
    }

    void "creates required request body parameter" () {
        def body = new RequestBody (
            'body', 'application/json',
            new ObjectDataType (new DataTypeName('FooRequestBody', 'FooRequestBody'), '',
                ['foo': new StringDataType ()], null, false, null),
            true, false, null)

        when:
        def codeSnippets = factory.create (body)

        then:
        def parameter = codeSnippets.first()
        parameter.text == "@RequestBody"
        parameter.imports.contains("org.springframework.web.bind.annotation.RequestBody")
    }

    void "creates optional request body parameter" () {
        def body = new RequestBody (
            'body', 'application/json',
            new ObjectDataType (new DataTypeName('FooRequestBody', 'FooRequestBody'), '',
                ['foo': new StringDataType ()], null, false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (body)

        then:
        def parameter = codeSnippets.first()
        parameter.text == "@RequestBody(required = false)"
        parameter.imports.contains("org.springframework.web.bind.annotation.RequestBody")
    }

    static DataTypeConstraints createConstraints(def defaultValue) {
        def c = new DataTypeConstraints()
        c.defaultValue = defaultValue
        c
    }

}
