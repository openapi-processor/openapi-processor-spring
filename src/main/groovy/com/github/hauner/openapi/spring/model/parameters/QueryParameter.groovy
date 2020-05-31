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

package com.github.hauner.openapi.spring.model.parameters

import com.github.hauner.openapi.core.model.parameters.ParameterBase
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType

/**
 * OpenAPI query parameter.
 *
 * @author Martin Hauner
 */
class QueryParameter extends ParameterBase {

    /**
     * controls if a parameter should have not have a {@code @RequestParam} annotation.
     */
    boolean withAnnotation () {
        // Map should be annotated
        if (dataType instanceof MappedMapDataType) {
            return true
        }

        // Pojo's should NOT be annotated
        if (dataType instanceof ObjectDataType) {
            return false
        }

        // usually a pojo....
        if (dataType instanceof MappedDataType) {
            return false
        }

        true
    }

    /**
     * controls if a {@code @RequestParam} should have any parameters.
     */
    boolean withParameters () {
        // Map should not have parameters
        if (dataType instanceof MappedMapDataType) {
            return false
        }

        // Pojo should not have parameters
        if (dataType instanceof ObjectDataType) {
            return false
        }

        true
    }

}
