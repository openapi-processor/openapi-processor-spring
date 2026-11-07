/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.core.model.datatypes.DataTypeConstraints
import io.openapiprocessor.core.model.datatypes.StringDataType
import io.openapiprocessor.core.model.parameters.PathParameter
import spock.lang.Specification

class PathParameterAnnotationFactorySpec extends Specification {
    def factory = new ParameterAnnotationFactory(new SpringFrameworkAnnotations())

    void "creates simple (required) path parameter" () {
        def param = new PathParameter(
            'foo', new StringDataType(), true, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def path = codeSnippets.first()
        path.text == '@PathVariable(name = "foo")'
        path.imports.contains("org.springframework.web.bind.annotation.PathVariable")
    }

    void "creates simple (optional) path parameter" () {
        def param = new PathParameter(
            'foo', new StringDataType(), false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def path = codeSnippets.first()
        path.text == '@PathVariable(name = "foo", required = false)'
        path.imports.contains("org.springframework.web.bind.annotation.PathVariable")
    }

    void "creates simple (optional with default) path parameter" () {
        def param = new PathParameter('foo',
            new StringDataType(
                'string',
                createConstraints ('bar'),
                false, null),
            false, false, null)

        when:
        def codeSnippets = factory.create (param)

        then:
        def path = codeSnippets.first()
        path.text == '@PathVariable(name = "foo", required = false, defaultValue = "bar")'
        path.imports.contains("org.springframework.web.bind.annotation.PathVariable")
    }

    static DataTypeConstraints createConstraints(def defaultValue) {
        def c = new DataTypeConstraints()
        c.defaultValue = defaultValue
        c
    }

}
