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
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.LocalDateDataType
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
            default: {new StringDataType()},
            date: {new LocalDateDataType ()}
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
     * converts an open api type (i.e. a {@code Schema}) to a java data type including nested types.
     * All (nested) $referenced types (except inline types) must be available from {@code dataTypes}.
     * {@code objectName} is used as the type name and it is added to the list of data types.
     *
     * @param schema the open api type
     * @param objectName type name for an object
     * @param dataTypes known object types
     * @return the resulting java data type
     */
    DataType convert (DataTypeInfo dataTypeInfo, DataTypes dataTypes) {
        Schema schema = dataTypeInfo.schema

        if (!schema) {
            new NoneDataType ()

        } else if (isArray (schema)) {
            createArrayDataType (dataTypeInfo, dataTypes)

        } else if (isRefObject (schema)) {
            def datatype = dataTypes.findRef (schema.$ref)
            if (datatype) {
                return datatype
            }

            DataTypeInfo objectDataTypeInfo = new DataTypeInfo(schema, getRefName (schema))
            createObjectDataType (objectDataTypeInfo, dataTypes)

        } else if (isObject (schema)) {
            createObjectDataType (dataTypeInfo, dataTypes)

        } else {
            createSimpleDataType (dataTypeInfo, dataTypes)
        }
    }

    private DataType createArrayDataType (DataTypeInfo dataTypeInfo, DataTypes dataTypes) {
        DataTypeInfo info = new DataTypeInfo(dataTypeInfo.schema.items, dataTypeInfo.name)
        DataType item = convert (info, dataTypes)

        def arrayType
        switch (getJavaType (dataTypeInfo.schema as ArraySchema)) {
            case Collection.name:
                arrayType = new CollectionDataType (item: item)
                break
            default:
                arrayType = new ArrayDataType (item: item)
        }

        dataTypes.add (objectName, arrayType)
        arrayType
    }

    private String getJavaType (ArraySchema schema) {
        if (!hasExtensions (schema)) {
            return null
        }

        schema.extensions.get ('x-java-type')
    }

    private DataType createObjectDataType (DataTypeInfo dataTypeInfo, DataTypes dataTypes) {
        Schema schema = dataTypeInfo.schema
        String objectName = dataTypeInfo.name

        def objectType = new ObjectDataType (
            type: objectName,
            pkg: [options.packageName, 'model'].join ('.')
        )

        schema.properties.each { Map.Entry<String, Schema> entry ->
            def propType
            if (isSimple (entry.value)) {
                // simple inline type,  no need to remember this
                propType = createSimpleDataType (entry.value)
            } else {
                def name = getNestedObjectName (objectName, entry.key)
                DataTypeInfo info = new DataTypeInfo(entry.value, name)
                propType = convert (info, dataTypes)
            }

            objectType.addObjectProperty (entry.key, propType)
        }

        dataTypes.add (objectType)
        objectType
    }

    private DataType createSimpleDataType (DataTypeInfo dataTypeInfo, DataTypes dataTypes) {
        Schema schema = dataTypeInfo.schema
        String name = dataTypeInfo.name

        def type = KNOWN_DATA_TYPES.get (schema.type)
        if (type == null) {
            throw new UnknownDataTypeException(schema.type, schema.format)
        }

        DataType simpleType
        if (schema.format) {
            simpleType = type."${schema.format}"(schema)
        } else {
            simpleType = type.default(schema)
        }

        dataTypes.add (name, simpleType)
        simpleType
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

    private String getNestedObjectName (String inlineObjectName, String propName) {
        inlineObjectName + propName.capitalize ()
    }

    boolean hasExtensions (ArraySchema schema) {
        schema.extensions != null
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

    private boolean isSimple (Schema schema) {
        ['string', 'integer', 'number', 'boolean'].contains (schema.type)
    }

    private String getRefName (Schema schema) {
        schema.$ref.substring (schema.$ref.lastIndexOf ('/') + 1)
    }

}
