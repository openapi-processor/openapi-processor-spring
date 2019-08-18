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

import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.CompositeDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.InlineObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import io.swagger.v3.oas.models.media.Schema

class DataTypeConverter {

    private static final KNOWN_DATA_TYPES = [
        string: [
            default: {new StringDataType()}
        ],
        integer: [
            default: {new IntegerDataType()},
            int32: {new IntegerDataType()},
            int64: {new LongDataType()}
        ],
        number: [
            default: {new FloatDataType()},
            float: {new FloatDataType()},
            double: {new DoubleDataType()}
        ],
        boolean: [
            default: {new BooleanDataType()}
        ],
        map: [
            default: {new InlineObjectDataType()}
        ]
    ]

    DataType none() {
        new NoneDataType()
    }

    /**
     * converts an open api type (i.e. a schema) to a java data type. If the schema is of type
     * object and declared inline than inlineObjectName is used as the type and it is added to
     * the list of object data types.
     *
     * @param schema the open api type
     * @param inlineObjectName type name for an inline object
     * @param dataTypes list of know object types
     * @return the resulting java data type
     */
    DataType convert(Schema schema, String inlineObjectName, List<DataType> dataTypes) {
        if (!schema) {
            return new NoneDataType()

        } else if (isInlineObject (schema)) {
            def inlineType = new CompositeDataType (type: inlineObjectName)

            schema.properties.each { Map.Entry<String, Schema> entry ->
                def propType = convert (entry.value,
                    getNestedInlineObjectName (inlineObjectName, entry.key), dataTypes)

                inlineType.addProperty (entry.key, propType)
            }

            dataTypes.add (inlineType)
            return inlineType
        }

        getDataType(schema)
    }

    private String getNestedInlineObjectName (String inlineObjectName, String propName) {
        inlineObjectName + propName.capitalize ()
    }

    private DataType getDataType (Schema schema) {
        def type = KNOWN_DATA_TYPES.get (schema.type)
        if (schema.format) {
            type."${schema.format}"(schema)
        } else {
            type.default(schema)
        }
    }

    private boolean isInlineObject (Schema schema) {
        schema.type == 'object'
    }


}
