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
import com.github.hauner.openapi.spring.model.datatypes.MapDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.InlineObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType

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
     * {@code dataTypeInfo} provides the type name used to add it to the list of data types (except
     * for inline types).
     *
     * @param dataTypeInfo the open api type with context information
     * @param dataTypes known object types
     * @return the resulting java data type
     */
    DataType convert (SchemaInfo dataTypeInfo, DataTypes dataTypes) {

        if (dataTypeInfo.isArray ()) {
            createArrayDataType (dataTypeInfo, dataTypes)

        } else if (dataTypeInfo.isRefObject ()) {
            def datatype = dataTypes.findRef (dataTypeInfo.ref)
            if (datatype) {
                return datatype
            }

            createObjectDataType (dataTypeInfo.buildForRef (), dataTypes)

        } else if (dataTypeInfo.isObject ()) {
            createObjectDataType (dataTypeInfo, dataTypes)

        } else {
            createSimpleDataType (dataTypeInfo, dataTypes)
        }
    }

    private DataType createArrayDataType (SchemaInfo dataTypeInfo, DataTypes dataTypes) {
        SchemaInfo itemDataTypeInfo = dataTypeInfo.buildForItem ()
        DataType item = convert (itemDataTypeInfo, dataTypes)

        def arrayType
        switch (dataTypeInfo.getXJavaType ()) {
            case Collection.name:
                arrayType = new CollectionDataType (item: item)
                break
            default:
                arrayType = new ArrayDataType (item: item)
        }

        if (dataTypeInfo.inline) {
            return arrayType
        }

        dataTypes.add (dataTypeInfo.name, arrayType)
        arrayType
    }

    private DataType createObjectDataType (SchemaInfo dataTypeInfo, DataTypes dataTypes) {
        def objectType
        switch (dataTypeInfo.getXJavaType ()) {
            case Map.name:
                objectType = new MapDataType ()
                dataTypes.add (dataTypeInfo.name, objectType)
                break

            default:
                objectType = new ObjectDataType (
                    type: dataTypeInfo.name,
                    pkg: [options.packageName, 'model'].join ('.')
                )

                dataTypeInfo.eachProperty { String propName, SchemaInfo propDataTypeInfo ->
                    def propType = convert (propDataTypeInfo, dataTypes)
                    objectType.addObjectProperty (propName, propType)
                }

                dataTypes.add (objectType)
        }

        objectType
    }

    private DataType createSimpleDataType (SchemaInfo dataTypeInfo, DataTypes dataTypes) {
        def type = KNOWN_DATA_TYPES.get (dataTypeInfo.type)
        if (type == null) {
            throw new UnknownDataTypeException(dataTypeInfo.type, dataTypeInfo.format)
        }

        DataType simpleType
        if (dataTypeInfo.format) {
            simpleType = type."${dataTypeInfo.format}"(dataTypeInfo)
        } else {
            simpleType = type.default(dataTypeInfo)
        }

        if (dataTypeInfo.inline) {
            return simpleType
        }

        dataTypes.add (dataTypeInfo.name, simpleType)
        simpleType
    }

}
