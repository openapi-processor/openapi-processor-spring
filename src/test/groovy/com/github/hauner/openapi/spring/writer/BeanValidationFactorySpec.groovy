/*
 * Copyright 2020 the original authors
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

import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.LocalDateDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import spock.lang.Specification
import spock.lang.Unroll

class BeanValidationFactorySpec extends Specification {

    BeanValidationFactory beanValidationFactory = new BeanValidationFactory()

    @Unroll
    void "check @Valid for Object (type: #type)"() {
        setup:
        DataType dataType = type.getDeclaredConstructor().newInstance()

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnnotation

        where:
        type              || resultImports              | resultAnnnotation
        ObjectDataType    || ["javax.validation.Valid"] | "@Valid"
        StringDataType    || []                         | ""
        IntegerDataType   || []                         | ""
        LongDataType      || []                         | ""
        ListDataType      || []                         | ""
        MappedDataType    || []                         | ""
        FloatDataType     || []                         | ""
        LocalDateDataType || []                         | ""
    }

    @Unroll
    void "check import @Size for String (minLength: #minLength, maxLength: #maxLength, type: #type)"() {
        setup:
        DataType dataType = type.getDeclaredConstructor().newInstance()
        dataType.constraints = new DataTypeConstraints()
        dataType.constraints.minLength = minLength
        dataType.constraints.maxLength = maxLength

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnotations

        where:
        type            | minLength | maxLength || resultImports                         | resultAnnotations
        StringDataType  | null      | null      || []                                    | ""
        StringDataType  | 1         | null      || ["javax.validation.constraints.Size"] | "@Size(min = 1)"
        StringDataType  | null      | 2         || ["javax.validation.constraints.Size"] | "@Size(max = 2)"
        StringDataType  | 1         | 2         || ["javax.validation.constraints.Size"] | "@Size(min = 1, max = 2)"
        IntegerDataType | 1         | null      || []                                    | ""
        IntegerDataType | null      | 2         || []                                    | ""
        IntegerDataType | 1         | 2         || []                                    | ""
        ListDataType    | 1         | 2         || []                                    | ""
    }

    @Unroll
    void "check import @Size for Collections (minItems: #minItems, maxItems: #maxItems, type: #type)"() {
        setup:
        DataType dataType = type.getDeclaredConstructor().newInstance()
        dataType.constraints = new DataTypeConstraints()
        dataType.constraints.minItems = minItems
        dataType.constraints.maxItems = maxItems

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnotations

        where:
        type           | minItems | maxItems || resultImports                         | resultAnnotations
        ArrayDataType  | null     | null     || []                                    | ""
        ArrayDataType  | 1        | null     || ["javax.validation.constraints.Size"] | "@Size(min = 1)"
        ArrayDataType  | null     | 2        || ["javax.validation.constraints.Size"] | "@Size(max = 2)"
        ArrayDataType  | 1        | 2        || ["javax.validation.constraints.Size"] | "@Size(min = 1, max = 2)"
        ListDataType   | null     | 2        || ["javax.validation.constraints.Size"] | "@Size(max = 2)"
        SetDataType    | 1        | 2        || ["javax.validation.constraints.Size"] | "@Size(min = 1, max = 2)"
        StringDataType | 1        | null     || []                                    | ""
        StringDataType | null     | 2        || []                                    | ""
        LongDataType   | 1        | 2        || []                                    | ""
    }

    @Unroll
    void "check import @NotNull (nullable: #nullable, type: #type)"() {
        setup:
        DataType dataType = type.getDeclaredConstructor().newInstance()
        dataType.constraints = new DataTypeConstraints()
        dataType.constraints.nullable = nullable

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnotations

        where:
        type            | nullable || resultImports                            | resultAnnotations
        IntegerDataType | null     || []                                       | ""
        IntegerDataType | true     || []                                       | ""
        IntegerDataType | false    || ["javax.validation.constraints.NotNull"] | "@NotNull"
        StringDataType  | null     || []                                       | ""
        StringDataType  | true     || []                                       | ""
        StringDataType  | false    || ["javax.validation.constraints.NotNull"] | "@NotNull"
        ListDataType    | null     || []                                       | ""
        ListDataType    | true     || []                                       | ""
        ListDataType    | false    || ["javax.validation.constraints.NotNull"] | "@NotNull"
    }

    @Unroll
    void "check import @DecimalMin (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum, type: #type)"() {
        setup:
        DataType dataType = type.getDeclaredConstructor().newInstance()
        dataType.constraints = new DataTypeConstraints()
        dataType.constraints.minimum = minimum
        dataType.constraints.exclusiveMinimum = exclusiveMinimum

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnotations

        where:
        type            | minimum | exclusiveMinimum || resultImports                               | resultAnnotations
        IntegerDataType | null    | null             || []                                          | ""
        IntegerDataType | null    | true             || []                                          | ""
        IntegerDataType | null    | false            || []                                          | ""
        IntegerDataType | 1       | null             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        IntegerDataType | 1       | true             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\", inclusive = false)"
        IntegerDataType | 1       | false            || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        LongDataType    | null    | null             || []                                          | ""
        LongDataType    | null    | true             || []                                          | ""
        LongDataType    | null    | false            || []                                          | ""
        LongDataType    | 1       | null             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        LongDataType    | 1       | true             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\", inclusive = false)"
        LongDataType    | 1       | false            || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        FloatDataType   | null    | null             || []                                          | ""
        FloatDataType   | null    | true             || []                                          | ""
        FloatDataType   | null    | false            || []                                          | ""
        FloatDataType   | 1       | null             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        FloatDataType   | 1       | true             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\", inclusive = false)"
        FloatDataType   | 1       | false            || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        DoubleDataType  | null    | null             || []                                          | ""
        DoubleDataType  | null    | true             || []                                          | ""
        DoubleDataType  | null    | false            || []                                          | ""
        DoubleDataType  | 1       | null             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        DoubleDataType  | 1       | true             || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\", inclusive = false)"
        DoubleDataType  | 1       | false            || ["javax.validation.constraints.DecimalMin"] | "@DecimalMin(value = \"1\")"
        StringDataType  | 1       | null             || []                                          | ""
    }

    @Unroll
    void "check import @DecimalMax (maximum: #maximum, exclusiveMaximum: #exclusiveMaximum, type: #type)"() {
        setup:
        DataType dataType = type.getDeclaredConstructor().newInstance()
        dataType.constraints = new DataTypeConstraints()
        dataType.constraints.maximum = maximum
        dataType.constraints.exclusiveMaximum = exclusiveMaximum

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnotations

        where:
        type            | maximum | exclusiveMaximum || resultImports                               | resultAnnotations
        IntegerDataType | null    | null             || []                                          | ""
        IntegerDataType | null    | true             || []                                          | ""
        IntegerDataType | null    | false            || []                                          | ""
        IntegerDataType | 1       | null             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        IntegerDataType | 1       | true             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\", inclusive = false)"
        IntegerDataType | 1       | false            || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        LongDataType    | null    | null             || []                                          | ""
        LongDataType    | null    | true             || []                                          | ""
        LongDataType    | null    | false            || []                                          | ""
        LongDataType    | 1       | null             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        LongDataType    | 1       | true             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\", inclusive = false)"
        LongDataType    | 1       | false            || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        FloatDataType   | null    | null             || []                                          | ""
        FloatDataType   | null    | true             || []                                          | ""
        FloatDataType   | null    | false            || []                                          | ""
        FloatDataType   | 1       | null             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        FloatDataType   | 1       | true             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\", inclusive = false)"
        FloatDataType   | 1       | false            || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        DoubleDataType  | null    | null             || []                                          | ""
        DoubleDataType  | null    | true             || []                                          | ""
        DoubleDataType  | null    | false            || []                                          | ""
        DoubleDataType  | 1       | null             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        DoubleDataType  | 1       | true             || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\", inclusive = false)"
        DoubleDataType  | 1       | false            || ["javax.validation.constraints.DecimalMax"] | "@DecimalMax(value = \"1\")"
        StringDataType  | 1       | null             || []                                          | ""
    }

    @Unroll
    void "check import @DecimalMin and @DecimalMax (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum maximum: #maximum, exclusiveMaximum: #exclusiveMaximum)"() {
        setup:
        DataType dataType = new DoubleDataType()
        dataType.constraints = new DataTypeConstraints()
        dataType.constraints.minimum = minimum
        dataType.constraints.exclusiveMinimum = exclusiveMinimum
        dataType.constraints.maximum = maximum
        dataType.constraints.exclusiveMaximum = exclusiveMaximum

        when:
        def imports = beanValidationFactory.collectImports(dataType)
        def annotations = beanValidationFactory.createAnnotations(dataType)

        then:
        imports == resultImports as Set<String>
        annotations == resultAnnotations

        where:
        minimum | exclusiveMinimum | maximum | exclusiveMaximum || resultImports                                                                          | resultAnnotations
        1       | false            | 2       | false            || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"] | "@DecimalMin(value = \"1\") @DecimalMax(value = \"2\")"
        1       | true             | 2       | false            || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"] | "@DecimalMin(value = \"1\", inclusive = false) @DecimalMax(value = \"2\")"
        1       | false            | 2       | true             || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"] | "@DecimalMin(value = \"1\") @DecimalMax(value = \"2\", inclusive = false)"
        1       | true             | 2       | true             || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"] | "@DecimalMin(value = \"1\", inclusive = false) @DecimalMax(value = \"2\", inclusive = false)"
        1       | true             | null    | true             || ["javax.validation.constraints.DecimalMin"]                                            | "@DecimalMin(value = \"1\", inclusive = false)"
        null    | true             | 2       | true             || ["javax.validation.constraints.DecimalMax"]                                            | "@DecimalMax(value = \"2\", inclusive = false)"
    }
}
