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
import com.github.hauner.openapi.spring.model.datatypes.DataTypeHelper
import com.github.hauner.openapi.spring.model.datatypes.DefaultDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType
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
        dataType                               | minLength | maxLength || annotationTypes
        DataTypeHelper.createString (null)     | null      | null      || []
        DataTypeHelper.createString (null)     | 1         | null      || ["javax.validation.constraints.Size"]
        DataTypeHelper.createString (null)     | null      | 2         || ["javax.validation.constraints.Size"]
        DataTypeHelper.createString (null)     | 1         | 2         || ["javax.validation.constraints.Size"]
        DataTypeHelper.createInteger (null)    | 1         | null      || []
        DataTypeHelper.createInteger (null)    | null      | 2         || []
        DataTypeHelper.createInteger (null)    | 1         | 2         || []
        DataTypeHelper.createList (null, null) | 1         | 2         || []
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
        dataType                                     | minItems | maxItems || annotationTypes
        DataTypeHelper.createArray (null, null)      | null     | null     || []
        DataTypeHelper.createArray (null, null)      | 1        | null     || ["javax.validation.constraints.Size"]
        DataTypeHelper.createArray (null, null)      | null     | 2        || ["javax.validation.constraints.Size"]
        DataTypeHelper.createArray (null, null)      | 1        | 2        || ["javax.validation.constraints.Size"]
        DataTypeHelper.createCollection (null, null) | null     | 2        || ["javax.validation.constraints.Size"]
        DataTypeHelper.createSet (null, null)        | 1        | 2        || ["javax.validation.constraints.Size"]
        DataTypeHelper.createString (null)           | 1        | null     || []
        DataTypeHelper.createString (null)           | null     | 2        || []
        DataTypeHelper.createLong (null)             | 1        | 2        || []
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
        dataType                               | nullable || annotationTypes
        DataTypeHelper.createInteger (null)    | null     || []
        DataTypeHelper.createInteger (null)    | true     || []
        DataTypeHelper.createInteger (null)    | false    || ["javax.validation.constraints.NotNull"]
        DataTypeHelper.createString (null)     | null     || []
        DataTypeHelper.createString (null)     | true     || []
        DataTypeHelper.createString (null)     | false    || ["javax.validation.constraints.NotNull"]
        DataTypeHelper.createList (null, null) | null     || []
        DataTypeHelper.createList (null, null) | true     || []
        DataTypeHelper.createList (null, null) | false    || ["javax.validation.constraints.NotNull"]
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
        dataType                            | minimum | exclusiveMinimum || annotationTypes
        DataTypeHelper.createInteger (null) | null    | null             || []
        DataTypeHelper.createInteger (null) | null    | true             || []
        DataTypeHelper.createInteger (null) | null    | false            || []
        DataTypeHelper.createInteger (null) | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createInteger (null) | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createInteger (null) | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createLong (null)    | null    | null             || []
        DataTypeHelper.createLong (null)    | null    | true             || []
        DataTypeHelper.createLong (null)    | null    | false            || []
        DataTypeHelper.createLong (null)    | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createLong (null)    | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createLong (null)    | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createFloat (null)   | null    | null             || []
        DataTypeHelper.createFloat (null)   | null    | true             || []
        DataTypeHelper.createFloat (null)   | null    | false            || []
        DataTypeHelper.createFloat (null)   | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createFloat (null)   | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createFloat (null)   | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createDouble (null)  | null    | null             || []
        DataTypeHelper.createDouble (null)  | null    | true             || []
        DataTypeHelper.createDouble (null)  | null    | false            || []
        DataTypeHelper.createDouble (null)  | 1       | null             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createDouble (null)  | 1       | true             || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createDouble (null)  | 1       | false            || ["javax.validation.constraints.DecimalMin"]
        DataTypeHelper.createString (null)  | 1       | null             || []
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
        dataType                            | maximum | exclusiveMaximum || annotationTypes
        DataTypeHelper.createInteger (null) | null    | null             || []
        DataTypeHelper.createInteger (null) | null    | true             || []
        DataTypeHelper.createInteger (null) | null    | false            || []
        DataTypeHelper.createInteger (null) | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createInteger (null) | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createInteger (null) | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createLong (null)    | null    | null             || []
        DataTypeHelper.createLong (null)    | null    | true             || []
        DataTypeHelper.createLong (null)    | null    | false            || []
        DataTypeHelper.createLong (null)    | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createLong (null)    | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createLong (null)    | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createFloat (null)   | null    | null             || []
        DataTypeHelper.createFloat (null)   | null    | true             || []
        DataTypeHelper.createFloat (null)   | null    | false            || []
        DataTypeHelper.createFloat (null)   | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createFloat (null)   | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createFloat (null)   | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createDouble (null)  | null    | null             || []
        DataTypeHelper.createDouble (null)  | null    | true             || []
        DataTypeHelper.createDouble (null)  | null    | false            || []
        DataTypeHelper.createDouble (null)  | 1       | null             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createDouble (null)  | 1       | true             || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createDouble (null)  | 1       | false            || ["javax.validation.constraints.DecimalMax"]
        DataTypeHelper.createString (null)  | 1       | null             || []
    }

    @Unroll
    void "check import @DecimalMin and @DecimalMax (minimum: #minimum, exclusiveMinimum: #exclusiveMinimum maximum: #maximum, exclusiveMaximum: #exclusiveMaximum)" () {
        setup:
        DataType dataType = DataTypeHelper.createDouble (null)
        dataType.constraints = new DataTypeConstraints ()
        dataType.constraints.minimum = minimum
        dataType.constraints.exclusiveMinimum = exclusiveMinimum
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
