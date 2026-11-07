/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.DataTypeName
import io.openapiprocessor.core.model.datatypes.ObjectDataType
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.spring.model.parameters.QueryParameter
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import spock.lang.Specification

class QueryParameterAnnotationWriterSpec extends Specification {
    def factory = new ParameterAnnotationFactory(new SpringFrameworkAnnotations())
    def target = new StringWriter()

    void "write simple (required) query parameter" () {
        def param = new QueryParameter(
            'foo', new StringDataType(), true, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def annotation = codeSnippets.first()

        annotation.text == '@RequestParam(name = "foo")'
        annotation.imports.contains("org.springframework.web.bind.annotation.RequestParam")
    }

    void "write simple (optional) query parameter" () {
        def param = new QueryParameter(
            'foo', new StringDataType(), false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def annotation = codeSnippets.first()
        annotation.text == '@RequestParam(name = "foo", required = false)'
        annotation.imports.contains("org.springframework.web.bind.annotation.RequestParam")
    }

    void "write simple (optional with default) query parameter" () {
        def param = new QueryParameter('foo',
            new StringDataType(
                'string',
                new DataTypeConstraints(defaultValue: 'bar'),
                false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def annotation = codeSnippets.first()
        annotation.text == '@RequestParam(name = "foo", required = false, defaultValue = "bar")'
        annotation.imports.contains("org.springframework.web.bind.annotation.RequestParam")
    }

    void "writes object query parameter without annotation" () {
        def param = new QueryParameter(
            'foo',
            new ObjectDataType (
                new DataTypeName('Foo', 'Foo'), '', [
                    foo1: new StringDataType (),
                    foo2: new StringDataType ()
                ], null, false, null
            ), false, false, null
        )

        when:
        def codeSnippets = factory.create (param)

        then:
        codeSnippets.isEmpty()
    }
}
