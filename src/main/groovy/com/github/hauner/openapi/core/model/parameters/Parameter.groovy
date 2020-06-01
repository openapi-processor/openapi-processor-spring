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

import com.github.hauner.openapi.core.model.datatypes.DataType

/**
 * Parameter model of an OpenAPI parameter.
 *
 * @author Martin Hauner
 */
interface Parameter {

    /**
     * the parameter name.
     *
     * @return the parameter name.
     */
    String getName ()

    /**
     * the data type of the parameter.

     * @return the data type of the parameter.
     */
    DataType getDataType ()

    /**
     * The imports required for the parameter data type.
     *
     * @return the imports of the parameter type.
     */
    Set<String> getDataTypeImports ()

    /**
     * Provides the parameters constraint details, if any.
     *
     * @return the constraint details or null if the parameter has no constraints
     */
    ParameterConstraints getConstraints()

    /**
     * Required or optional parameter?
     *
     * @return true if required, false otherwise
     */
    boolean isRequired ()

    /**
     * Create annotation? Some parameters should not have a parameter annotation.
     *
     * @return true if the parameter should have an annotation, else false
     */
    abstract boolean withAnnotation ()

    /**
     * Create annotation with parameters? Some parameters should have a parameter annotation but
     * without any parameters to the annotation.
     *
     * @return true if the annotation itself should have parameters, false otherwise
     */
    abstract boolean withParameters ()

}
