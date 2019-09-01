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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.datatypes.DataType
import io.swagger.v3.oas.models.media.Schema

/**
 * Collects the object schemas from the OpenAPI description.
 *
 * @author Martin Hauner
 */
class SchemaCollector {

    DataTypeConverter converter

    void collect(Map<String, Schema> schemas, DataTypes dataTypes) {

        // avoid forward references, so the data type converter has seen (& stored) all nested
        // data types before they are used.
        def sortedSchemas = schemas.sort { left, right ->
            Set<String> leftRefs = []
            Set<String> rightRefs = []

            collectRefs (left.value, leftRefs)
            collectRefs (right.value, rightRefs)

            if (leftRefs.empty && rightRefs.empty) {
                return 0
            }

            if (leftRefs.empty && !rightRefs.empty) {
                return -1
            }

            if (!leftRefs.empty && rightRefs.empty) {
                return 1
            }

            if (rightRefs.contains (left.key)) {
                return -1
            }

            if (leftRefs.contains (right.key)) {
                return 1
            }

            return 0
        }

        sortedSchemas.each { Map.Entry<String, Schema> entry ->
            String name = entry.key
            Schema schema = entry.value

            DataType type = dataTypes.find (name)
            if (!type) {
                converter.convert (schema, name, dataTypes)
            }
        }
    }

    private void collectRefs (Schema schema, Set<String> refs) {
        if (schema.$ref != null) {
            refs.add (schema.$ref.substring ('#/components/schemas/'.length ()))
        }

        if (schema.properties == null) {
            return
        }

        schema.properties.values ().each {
            collectRefs (it, refs)
        }
    }

}
