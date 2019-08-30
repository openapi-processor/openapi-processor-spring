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

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.InlineObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema

/**
 * Converter to map OpenAPI schemas to Java data types.
 *
 * @author Martin Hauner
 */
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

    private ApiOptions options

    DataTypeConverter(ApiOptions options) {
        this.options = options
    }

    DataType none() {
        new NoneDataType()
    }

    /**
     * converts an open api type (i.e. a schema) to a java data type. If the schema is of type
     * object the objectName is used as the type and it is added to the list of object data types.
     *
     * @param schema the open api type
     * @param objectName type name for an object
     * @param dataTypes list of know object types
     * @return the resulting java data type
     */
    DataType convert(Schema schema, String objectName, List<DataType> dataTypes) {
        if (!schema) {
            new NoneDataType ()

        } else if (isArray (schema)) {
            createArrayDataType (schema as ArraySchema, objectName, dataTypes)

        } else if (isObject (schema)) {
            createObjectDataType (schema, objectName, dataTypes)

        } else if (isRefObject (schema)) {
            def datatype = getDataType (schema.$ref, dataTypes)
            if (datatype) {
                return datatype
            }

            createObjectDataType (schema, schema.name, dataTypes)

        } else {
            createSimpleDataType (schema)
        }
    }

    private DataType createArrayDataType (ArraySchema schema, String objectName, List<DataType> dataTypes) {
        DataType item = convert (schema.items, objectName, dataTypes)
        def array = new ArrayDataType(item: item)
        array
    }

    private DataType createObjectDataType (Schema schema, String objectName, List<DataType> dataTypes) {
        def objectType = new ObjectDataType (
            type: objectName,
            pkg: [options.packageName, 'model'].join ('.')
        )

        schema.properties.each { Map.Entry<String, Schema> entry ->
            def propType = convert (entry.value,
                getNestedObjectName (objectName, entry.key), dataTypes)

            objectType.addObjectProperty (entry.key, propType)
        }

        dataTypes.add (objectType)
        objectType
    }

    private DataType createSimpleDataType (Schema schema) {
        def type = KNOWN_DATA_TYPES.get (schema.type)
        if (type == null) {
            throw new UnknownDataTypeException(schema.type, schema.format)
        }

        if (schema.format) {
            type."${schema.format}"(schema)
        } else {
            type.default(schema)
        }
    }

    private DataType getDataType (String ref, List<DataType> dataTypes) {
        def idx = ref.lastIndexOf ('/')
        def path = ref.substring (0, idx + 1)
        def name = ref.substring (idx + 1)

        if (path != '#/components/schemas/') {
            return null
        }

       dataTypes.find { it.name == name }
    }

    private String getNestedObjectName (String inlineObjectName, String propName) {
        inlineObjectName + propName.capitalize ()
    }

    private boolean isArray (Schema schema) {
        schema.type == 'array'
    }

    private boolean isObject (Schema schema) {
        schema.type == 'object'
    }

    private boolean isRefObject (Schema schema) {
        schema.$ref != null
    }

}
