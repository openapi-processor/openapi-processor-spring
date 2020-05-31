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

package com.github.hauner.openapi.core.model.parameters

import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.parameters.ParameterConstraints

abstract class ParameterBase implements Parameter {
    String name
    boolean required
    DataType dataType

    @Override
    @Deprecated
    String getAnnotationName () {
        return null // dummy
    }

    @Override
    @Deprecated
    String getAnnotation () {
        "@${annotationName}"
    }

    @Override
    @Deprecated
    String getAnnotationWithPackage () {
        null // dummy
    }

    /**
     * The imports required for the parameter data type.
     *
     * @return the imports of the parameter type.
     */
    @Override
    Set<String> getDataTypeImports () {
        dataType.imports
    }

    /**
     * Provides the parameters constraint details, if any.
     *
     * @return the constraint details or null if the parameter has no constraints
     */
    @Override
    ParameterConstraints getConstraints() {
        new ParameterConstraints(constraints: dataType.constraints)
    }

    /**
     * Required or optional parameter?
     *
     * @return true if required, false otherwise
     */
    @Override
    boolean isRequired () {
        required
    }

}
