/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.CookieParameter
import spock.lang.Specification

class CookieParameterAnnotationFactorySpec extends Specification {
    def factory = new ParameterAnnotationFactory(new SpringFrameworkAnnotations())

    void "create simple (required) cookie parameter" () {
        def param = new CookieParameter(
            'foo', new StringDataType(), true, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def cookie = codeSnippets.first()
        cookie.text == '@CookieValue(name = "foo")'
        cookie.imports.containsAll(["org.springframework.web.bind.annotation.CookieValue"])
    }

    void "create simple (optional) cookie parameter" () {
        def param = new CookieParameter(
            'foo', new StringDataType(), false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def cookie = codeSnippets.first()
        cookie.text == '@CookieValue(name = "foo", required = false)'
        cookie.imports.containsAll(["org.springframework.web.bind.annotation.CookieValue"])
    }

    void "create simple (optional with default) cookie parameter" () {
        def param = new CookieParameter('foo',
            new StringDataType('string',
                createConstraints ('bar'),
                false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def cookie = codeSnippets.first()
        cookie.text == '@CookieValue(name = "foo", required = false, defaultValue = "bar")'
        cookie.imports.containsAll(["org.springframework.web.bind.annotation.CookieValue"])
    }

    static DataTypeConstraints createConstraints(def defaultValue) {
        def c = new DataTypeConstraints()
        c.defaultValue = defaultValue
        c
    }

}
