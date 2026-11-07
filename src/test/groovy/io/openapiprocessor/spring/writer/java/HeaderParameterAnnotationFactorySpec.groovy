/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.HeaderParameter
import spock.lang.Specification

class HeaderParameterAnnotationFactorySpec extends Specification {
    def factory = new ParameterAnnotationFactory(new SpringFrameworkAnnotations())

    void "create simple (required) header parameter" () {
        def param = new HeaderParameter(
            'foo', new StringDataType(), true, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def header = codeSnippets.first()
        header.text == '@RequestHeader(name = "foo")'
        header.imports.containsAll(["org.springframework.web.bind.annotation.RequestHeader"])
    }

    void "create simple (optional) header parameter" () {
        def param = new HeaderParameter(
            'foo', new StringDataType(), false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def header = codeSnippets.first()
        header.text == '@RequestHeader(name = "foo", required = false)'
        header.imports.containsAll(["org.springframework.web.bind.annotation.RequestHeader"])
    }

    void "create simple (optional with default) header parameter" () {
        def param = new HeaderParameter('foo',
            new StringDataType('string',
                createConstraints ('bar'),
                false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def header = codeSnippets.first()
        header.text == '@RequestHeader(name = "foo", required = false, defaultValue = "bar")'
        header.imports.containsAll(["org.springframework.web.bind.annotation.RequestHeader"])
    }

    static DataTypeConstraints createConstraints(def defaultValue) {
        def c = new DataTypeConstraints()
        c.defaultValue = defaultValue
        c
    }

}
