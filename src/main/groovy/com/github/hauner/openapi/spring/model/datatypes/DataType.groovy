/*
 * Copyright 2019 the original authors
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

package com.github.hauner.openapi.spring.model.datatypes

/**
 * Data type description of a Java data type.
 *
 * @author Martin Hauner
 */
trait /*interface*/ DataType {

    /**
     * The Java type name without package.
     *
     * @return the type name.
     */
    abstract String getName ()

    /**
     * The package of this type without class.
     */
    abstract String getPackageName ()

    /**
     * Provides the import(s) of this type, usually a single import. If it is a generic type it will
     * add another import for each generic parameter.
     *
     * @return import of this type.
     */
    abstract Set<String> getImports ()

    /**
     * Provides the list of imports for the types referenced by this this type.
     *
     * @return the referenced import list.
     */
    abstract Set<String> getReferencedImports ()

    /**
     * Provides the constraint information of the data type.
     *
     * @return the constraints or null if there are no constraints.
     */
    DataTypeConstraints getConstraints() {
        null
    }

}
