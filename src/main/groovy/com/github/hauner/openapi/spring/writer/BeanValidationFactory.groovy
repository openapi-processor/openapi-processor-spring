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

import com.github.hauner.openapi.core.model.datatypes.ArrayDataType
import com.github.hauner.openapi.core.model.datatypes.CollectionDataType
import com.github.hauner.openapi.core.model.datatypes.DataType
import com.github.hauner.openapi.core.model.datatypes.DoubleDataType
import com.github.hauner.openapi.core.model.datatypes.FloatDataType
import com.github.hauner.openapi.core.model.datatypes.IntegerDataType
import com.github.hauner.openapi.core.model.datatypes.ListDataType
import com.github.hauner.openapi.core.model.datatypes.LongDataType
import com.github.hauner.openapi.core.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.core.model.datatypes.ObjectDataType
import com.github.hauner.openapi.core.model.datatypes.SetDataType
import com.github.hauner.openapi.core.model.datatypes.StringDataType

/**
 * @author Bastian Wilhelm
 */
class BeanValidationFactory {
    Set<String> collectImports (DataType dataType) {
        Set<String> imports = []

        if (useValid (dataType)) {
            imports.add ("javax.validation.Valid")
        }

        if (useNotNull (dataType)) {
            imports.add ("javax.validation.constraints.NotNull")
        }

        if (useSize (dataType)) {
            imports.add ("javax.validation.constraints.Size")
        }

        if (useDecimalMin (dataType)) {
            imports.add ("javax.validation.constraints.DecimalMin")
        }

        if (useDecimalMax (dataType)) {
            imports.add ("javax.validation.constraints.DecimalMax")
        }

        imports
    }

    private static useValid (DataType dataType) {
        dataType instanceof ObjectDataType
    }

    private static useNotNull (DataType dataType) {
        dataType.constraints?.nullable == false
    }

    private static useSize (DataType dataType) {
        ( // Lists
            (
                dataType instanceof CollectionDataType
                    || dataType instanceof ListDataType
                    || dataType instanceof SetDataType
                    || dataType instanceof ArrayDataType
                    || dataType instanceof MappedMapDataType
            ) && (
                dataType.constraints?.maxItems != null
                    || dataType.constraints?.minItems
            )
        ) || ( // String
            (
                dataType instanceof StringDataType
            ) && (
                dataType.constraints?.maxLength != null
                    || dataType.constraints?.minLength
            )
        )
    }

    private static useDecimalMax (DataType dataType) {
        (
            dataType instanceof DoubleDataType
                || dataType instanceof FloatDataType
                || dataType instanceof IntegerDataType
                || dataType instanceof LongDataType
        ) && (
            dataType.constraints?.maximum != null
        )
    }

    private static useDecimalMin (DataType dataType) {
        (
            dataType instanceof DoubleDataType
                || dataType instanceof FloatDataType
                || dataType instanceof IntegerDataType
                || dataType instanceof LongDataType
        ) && (
            dataType.constraints?.minimum != null
        )
    }

    String createAnnotations (DataType dataType) {
        List<String> annotations = []
        if (useValid (dataType)) {
            annotations.add (createValidAnnotation ())
        }

        if (useNotNull (dataType)) {
            annotations.add (createNotNullAnnotation ())
        }

        if (useSize (dataType)) {
            annotations.add (createSize (dataType))
        }

        if (useDecimalMin (dataType)) {
            annotations.add (createDecimalMinAnnotation (dataType))
        }

        if (useDecimalMax (dataType)) {
            annotations.add (createDecimalMaxAnnotation (dataType))
        }

        annotations.join (' ')
    }

    private static String createValidAnnotation () {
        "@Valid"
    }

    private static String createNotNullAnnotation () {
        "@NotNull"
    }

    private static String createDecimalMinAnnotation (DataType dataType) {
        def minimum = dataType.constraints.minimum
        if (dataType.constraints.exclusiveMinimum) {
            "@DecimalMin(value = \"${minimum}\", inclusive = false)"
        } else {
            "@DecimalMin(value = \"${minimum}\")"
        }
    }

    private static String createDecimalMaxAnnotation (DataType dataType) {
        def maximum = dataType.constraints.maximum
        if (dataType.constraints.exclusiveMaximum) {
            "@DecimalMax(value = \"${maximum}\", inclusive = false)"
        } else {
            "@DecimalMax(value = \"${maximum}\")"
        }
    }

    private static String createSize (DataType dataType) {
        final def minLength
        final def maxLength
        if (dataType instanceof StringDataType) {
            minLength = dataType.constraints.minLength
            maxLength = dataType.constraints.maxLength
        } else {
            minLength = dataType.constraints.minItems
            maxLength = dataType.constraints.maxItems
        }

        createSize(minLength, maxLength)
    }

    private static String createSize (def min, def max) {
        if(min && max) {
            "@Size(min = ${min}, max = ${max})"
        } else if (min) {
            "@Size(min = ${min})"
        } else {
            "@Size(max = ${max})"
        }
    }
}
