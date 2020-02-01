/*
 * Copyright 2019-2020 the original authors
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
import com.github.hauner.openapi.spring.model.datatypes.DataTypeHelper
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName

/**
 * @author Bastian Wilhelm
 */
class BeanValidationGenerator {
    private static useValid (DataType dataType) {
        dataType instanceof ObjectDataType
    }

    private static useNotNull (DataType dataType) {
        dataType.constraints?.nullable == false
    }

    private static useSize (DataType dataType) {
        ( // Lists
            (

                DataTypeHelper.isCollection (dataType)
                    || DataTypeHelper.isList (dataType)
                    || DataTypeHelper.isSet (dataType)
                    || dataType instanceof ArrayDataType
                    || DataTypeHelper.isMap (dataType)
            ) && (
                dataType.constraints?.maxItems
                    || dataType.constraints?.minItems
            )
        ) || ( // String
            (
                DataTypeHelper.isString (dataType)
            ) && (
                dataType.constraints?.maxLength
                    || dataType.constraints?.minLength
            )
        )
    }

    private static useDecimalMax (DataType dataType) {
        (
            DataTypeHelper.isDouble (dataType)
                || DataTypeHelper.isFloat (dataType)
                || DataTypeHelper.isInteger (dataType)
                || DataTypeHelper.isLong (dataType)
        ) && (
            dataType.constraints?.maximum
        )
    }

    private static useDecimalMin (DataType dataType) {
        (
            DataTypeHelper.isDouble (dataType)
                || DataTypeHelper.isFloat (dataType)
                || DataTypeHelper.isInteger (dataType)
                || DataTypeHelper.isLong (dataType)
        ) && (
            dataType.constraints?.minimum
        )
    }

    List<AnnotationSpec> generateAnnotations (DataType dataType) {
        List<AnnotationSpec> annotations = []

        if (useValid (dataType)) {
            annotations.add (generateValidAnnotation ())
        }

        if (useNotNull (dataType)) {
            annotations.add (generateNotNullAnnotation ())
        }

        if (useSize (dataType)) {
            annotations.add (generateSize (dataType))
        }

        if (useDecimalMin (dataType)) {
            annotations.add (generateDecimalMinAnnotation (dataType))
        }

        if (useDecimalMax (dataType)) {
            annotations.add (generateDecimalMaxAnnotation (dataType))
        }

        annotations
    }

    private static AnnotationSpec generateValidAnnotation () {
        AnnotationSpec.builder (ClassName.get ('javax.validation', 'Valid'))
            .build ()
    }

    private static AnnotationSpec generateNotNullAnnotation () {
        AnnotationSpec.builder (ClassName.get ('javax.validation.constraints', 'NotNull'))
            .build ()
    }

    private static AnnotationSpec generateSize (DataType dataType) {
        def annotationSpecBuilder = AnnotationSpec.builder (ClassName.get ('javax.validation.constraints', 'Size'))
        if (DataTypeHelper.isString (dataType)) {
            if (dataType.constraints.minLength) {
                annotationSpecBuilder.addMember ('min', '$L', dataType.constraints.minLength)
            }
            if (dataType.constraints.maxLength) {
                annotationSpecBuilder.addMember ('max', '$L', dataType.constraints.maxLength)
            }
        } else {
            if (dataType.constraints.minItems) {
                annotationSpecBuilder.addMember ('min', '$L', dataType.constraints.minItems)
            }
            if (dataType.constraints.maxItems) {
                annotationSpecBuilder.addMember ('max', '$L', dataType.constraints.maxItems)
            }
        }

        annotationSpecBuilder.build ()
    }

    private static AnnotationSpec generateDecimalMinAnnotation (DataType dataType) {
        def annotationSpecBuilder = AnnotationSpec.builder (
            ClassName.get ('javax.validation.constraints', 'DecimalMin')
        )

        annotationSpecBuilder.addMember ('value', '$S', dataType.constraints.minimum)

        if (dataType.constraints.exclusiveMinimum) {
            annotationSpecBuilder.addMember ('inclusive', '$L', false)
        }

        annotationSpecBuilder.build ()
    }

    private static AnnotationSpec generateDecimalMaxAnnotation (DataType dataType) {
        def annotationSpecBuilder = AnnotationSpec.builder (
            ClassName.get ('javax.validation.constraints', 'DecimalMax')
        )

        annotationSpecBuilder.addMember ('value', '$S', dataType.constraints.maximum)

        if (dataType.constraints.exclusiveMaximum) {
            annotationSpecBuilder.addMember ('inclusive', '$L', false)
        }

        annotationSpecBuilder.build ()
    }
}
