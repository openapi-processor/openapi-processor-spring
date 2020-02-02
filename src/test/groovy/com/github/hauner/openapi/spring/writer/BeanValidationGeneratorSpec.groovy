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
import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints

import com.github.hauner.openapi.spring.model.datatypes.DefaultDataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import spock.lang.Specification
import spock.lang.Unroll

class BeanValidationGeneratorSpec extends Specification {

    BeanValidationGenerator beanValidationGenerator = new BeanValidationGenerator ()

    @Unroll
    void "check @Valid for Object (type: #type)" () {
        setup:
        DataType dataType = type.getDeclaredConstructor ().newInstance ()

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
            }

        }

        where:
        type            || annotationTypes
        ObjectDataType  || ["javax.validation.Valid"]
        DefaultDataType || []
        MappedDataType  || []
    }

    @Unroll
    void "check import @Size for String (minLength: #minLength, maxLength: #maxLength, dataType: #dataType)" () {
        setup:
        dataType.constraints = new DataTypeConstraints ()
        dataType.constraints.minLength = minLength
        dataType.constraints.maxLength = maxLength

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
                annotationSpec.members.get ('min').toString () == minLength
                annotationSpec.members.get ('max').toString () == maxLength
            }

        }

        where:
        dataType                      | minLength | maxLength || annotationTypes
        new StringDataType ()         | null      | null      || []
        new StringDataType ()         | 1         | null      || ["javax.validation.constraints.Size"]
        new StringDataType ()         | null      | 2         || ["javax.validation.constraints.Size"]
        new StringDataType ()         | 1         | 2         || ["javax.validation.constraints.Size"]
        new IntegerDataType ()        | 1         | null      || []
        new IntegerDataType ()        | null      | 2         || []
        new IntegerDataType ()        | 1         | 2         || []
        new ListDataType (null, null) | 1         | 2         || []
    }

    @Unroll
    void "check import @Size for Collections (minItems: #minItems, maxItems: #maxItems, dataType: #dataType)" () {
        setup:
        dataType.constraints = new DataTypeConstraints ()
        dataType.constraints.minItems = minItems
        dataType.constraints.maxItems = maxItems

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
                annotationSpec.members.get ('min').toString () == minItems
                annotationSpec.members.get ('max').toString () == maxItems
            }

        }

        where:
        dataType                            | minItems | maxItems || annotationTypes
        new ArrayDataType (null, null)      | null     | null     || []
        new ArrayDataType (null, null)      | 1        | null     || ["javax.validation.constraints.Size"]
        new ArrayDataType (null, null)      | null     | 2        || ["javax.validation.constraints.Size"]
        new ArrayDataType (null, null)      | 1        | 2        || ["javax.validation.constraints.Size"]
        new CollectionDataType (null, null) | null     | 2        || ["javax.validation.constraints.Size"]
        new SetDataType (null, null)        | 1        | 2        || ["javax.validation.constraints.Size"]
        new StringDataType ()               | 1        | null     || []
        new StringDataType ()               | null     | 2        || []
        new LongDataType ()                 | 1        | 2        || []
    }

    @Unroll
    void "check import @NotNull (nullable: #nullable, dataType: #dataType)" () {
        setup:
        dataType.constraints = new DataTypeConstraints ()
        dataType.constraints.nullable = nullable

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
            }

        }

        where:
        dataType                      | nullable || annotationTypes
        new IntegerDataType ()        | null     || []
        new IntegerDataType ()        | true     || []
        new IntegerDataType ()        | false    || ["javax.validation.constraints.NotNull"]
        new StringDataType ()         | null     || []
        new StringDataType ()         | true     || []
        new StringDataType ()         | false    || ["javax.validation.constraints.NotNull"]
        new ListDataType (null, null) | null     || []
        new ListDataType (null, null) | true     || []
        new ListDataType (null, null) | false    || ["javax.validation.constraints.NotNull"]
    }

    @Unroll
    void "check import @DecimalMin (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum, dataType: #dataType)" () {
        setup:
        dataType.constraints = new DataTypeConstraints ()
        dataType.constraints.minimum = minimum
        dataType.constraints.exclusiveMinimum = exclusiveMinimum

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
                annotationSpec.members.get ('value').toString () == '"' + minimum + '"'
                if (exclusiveMinimum == null) {
                    annotationSpec.members.get ('inclusive').toString () == null
                } else {
                    annotationSpec.members.get ('inclusive').toString () == !exclusiveMinimum
                }
            }

        }

        where:
        dataType               | minimum | exclusiveMinimum || annotationTypes
        new IntegerDataType () | null    | null             || []
        new IntegerDataType () | null    | true             || []
        new IntegerDataType () | null    | false            || []
        new IntegerDataType () | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        new IntegerDataType () | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        new IntegerDataType () | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        new LongDataType ()    | null    | null             || []
        new LongDataType ()    | null    | true             || []
        new LongDataType ()    | null    | false            || []
        new LongDataType ()    | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        new LongDataType ()    | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        new LongDataType ()    | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        new FloatDataType ()   | null    | null             || []
        new FloatDataType ()   | null    | true             || []
        new FloatDataType ()   | null    | false            || []
        new FloatDataType ()   | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        new FloatDataType ()   | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        new FloatDataType ()   | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        new DoubleDataType ()  | null    | null             || []
        new DoubleDataType ()  | null    | true             || []
        new DoubleDataType ()  | null    | false            || []
        new DoubleDataType ()  | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        new DoubleDataType ()  | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        new DoubleDataType ()  | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        new StringDataType ()  | 1       | null             || []
    }

    @Unroll
    void "check import @DecimalMax (maximum: #maximum, exclusiveMaximum: #exclusiveMaximum, dataType: #dataType)" () {
        setup:
        dataType.constraints = new DataTypeConstraints ()
        dataType.constraints.maximum = maximum
        dataType.constraints.exclusiveMaximum = exclusiveMaximum

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
                annotationSpec.members.get ('value').toString () == '"' + maximum + '"'
                if (exclusiveMaximum == null) {
                    annotationSpec.members.get ('inclusive').toString () == null
                } else {
                    annotationSpec.members.get ('inclusive').toString () == !exclusiveMaximum
                }
            }
        }

        where:
        dataType               | maximum | exclusiveMaximum || annotationTypes
        new IntegerDataType () | null    | null             || []
        new IntegerDataType () | null    | true             || []
        new IntegerDataType () | null    | false            || []
        new IntegerDataType () | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        new IntegerDataType () | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        new IntegerDataType () | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        new LongDataType ()    | null    | null             || []
        new LongDataType ()    | null    | true             || []
        new LongDataType ()    | null    | false            || []
        new LongDataType ()    | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        new LongDataType ()    | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        new LongDataType ()    | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        new FloatDataType ()   | null    | null             || []
        new FloatDataType ()   | null    | true             || []
        new FloatDataType ()   | null    | false            || []
        new FloatDataType ()   | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        new FloatDataType ()   | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        new FloatDataType ()   | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        new DoubleDataType ()  | null    | null             || []
        new DoubleDataType ()  | null    | true             || []
        new DoubleDataType ()  | null    | false            || []
        new DoubleDataType ()  | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        new DoubleDataType ()  | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        new DoubleDataType ()  | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        new StringDataType ()  | 1       | null             || []
    }

    @Unroll
    void "check import @DecimalMin and @DecimalMax (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum maximum: #maximum, exclusiveMaximum: #exclusiveMaximum)" () {
        setup:
        DataTypeConstraints constraints = new DataTypeConstraints ()
        constraints.minimum = minimum
        constraints.exclusiveMinimum = exclusiveMinimum
        constraints.maximum = maximum
        constraints.exclusiveMaximum = exclusiveMaximum
        DataType dataType = new DoubleDataType (constraints: constraints)

        when:
        def annotationSpecs = beanValidationGenerator.generateAnnotations (dataType)

        then:
        annotationSpecs.size () == annotationTypes.size ()
        if (annotationTypes.size () > 0) {
            for (int i = 0; i < annotationTypes.size (); i++) {
                def annotationSpec = annotationSpecs.find { it.type.canonicalName == annotationTypes [i] }
                annotationSpec != null
                if (annotationSpec.type.canonicalName == 'javax.validation.constraints.DecimalMin') {
                    annotationSpec.members.get ('value').toString () == '"' + minimum + '"'
                    if (exclusiveMinimum == null) {
                        annotationSpec.members.get ('inclusive').toString () == null
                    } else {
                        annotationSpec.members.get ('inclusive').toString () == !exclusiveMinimum
                    }
                }
                if (annotationSpec.type.canonicalName == 'javax.validation.constraints.DecimalMax') {
                    annotationSpec.members.get ('value').toString () == '"' + maximum + '"'
                    if (exclusiveMaximum == null) {
                        annotationSpec.members.get ('inclusive').toString () == null
                    } else {
                        annotationSpec.members.get ('inclusive').toString () == !exclusiveMaximum
                    }
                }
            }
        }

        where:
        minimum | exclusiveMinimum | maximum | exclusiveMaximum || annotationTypes
        1       | false            | 2       | false            || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"]
        1       | true             | 2       | false            || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"]
        1       | false            | 2       | true             || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"]
        1       | true             | 2       | true             || ["javax.validation.constraints.DecimalMin", "javax.validation.constraints.DecimalMax"]
        1       | true             | null    | true             || ["javax.validation.constraints.DecimalMin"]
        null    | true             | 2       | true             || ["javax.validation.constraints.DecimalMax"]
    }
}
