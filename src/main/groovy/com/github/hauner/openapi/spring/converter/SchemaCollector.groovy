/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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

import com.github.hauner.openapi.spring.model.datatypes.DataType
import io.swagger.v3.oas.models.media.Schema

class SchemaCollector {

    DataTypeConverter converter

    List<DataType> collect(Map<String, Schema> schemas) {
        List<DataType> dataTypes = []

        schemas.each { Map.Entry<String, Schema> entry ->
            String name = entry.key
            Schema schema = entry.value

            DataType type = findDataType (name, dataTypes)
            if (!type) {
                converter.convert (schema, name, dataTypes)
            }
        }

        dataTypes
    }

    private DataType findDataType(String name, List<DataType> dataTypes) {
        dataTypes.find { it.name.toLowerCase () == name.toLowerCase () }
    }

}
