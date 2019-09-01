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

package com.github.hauner.openapi.spring.model

import com.github.hauner.openapi.spring.model.datatypes.DataType

/**
 * Container for Java data types from OpenAPI '#/component/schemas'.
 *
 * @author Martin Hauner
 */
class DataTypes {

    private Map<String, DataType> types = [:]

    // needed?
    List<DataType> getDataTypes () {
        types.values () as List<DataType>
    }

    // needed?
    void add (List<DataType> dataTypes) {
        dataTypes.each {
            types.put (it.name, it)
        }
    }

    /**
     * store data type.
     *
     * @param dataType the source data type
     */
    void add (DataType dataType) {
        types.put (dataType.name, dataType)
    }


    /**
     * find data type by name.
     *
     * @param name the name
     * @return the data type or null if not found
     */
    DataType find (String name) {
        types.get (name)
    }

    /**
     * find data type by $ref name.
     *
     * @param ref the OpenAPI $ref
     * @return the data type or null if not found
     */
    DataType findRef (String ref) {
        def idx = ref.lastIndexOf ('/')
        def path = ref.substring (0, idx + 1)
        def name = ref.substring (idx + 1)

        if (path != '#/components/schemas/') {
            return null
        }

        types.get (name)
    }

    int size () {
        types.size ()
    }

}
