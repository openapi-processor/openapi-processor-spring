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
import com.github.hauner.openapi.spring.generatr.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.generatr.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.generatr.mapping.TypeMapping
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.LocalDateDataType
import com.github.hauner.openapi.spring.model.datatypes.MapDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.InlineObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
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

            def refTypeInfo = dataTypeInfo.buildForRef ()
            convert (refTypeInfo, dataTypes)

        } else if (dataTypeInfo.isObject ()) {
            createObjectDataType (dataTypeInfo, dataTypes)

        } else {
            createSimpleDataType (dataTypeInfo, dataTypes)
        }
    }

    private DataType createArrayDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        SchemaInfo itemSchemaInfo = schemaInfo.buildForItem ()
        DataType item = convert (itemSchemaInfo, dataTypes)

        def arrayType
        switch (getArrayDataType(schemaInfo)) {
            case Collection.name:
                arrayType = new CollectionDataType (item: item)
                break
            case List.name:
                arrayType = new ListDataType (item: item)
                break
            case Set.name:
                arrayType = new SetDataType (item: item)
                break
            default:
                arrayType = new ArrayDataType (item: item)
        }

        arrayType
    }

    private DataType createObjectDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        def objectType

        String targetTypeName = getObjectDataType (schemaInfo)
        if (targetTypeName) {
            objectType = new MappedDataType (
                type: targetTypeName.substring (targetTypeName.lastIndexOf ('.') + 1),
                pkg: targetTypeName.substring (0, targetTypeName.lastIndexOf ('.'))
            )

            dataTypes.add (schemaInfo.name, objectType)
            return objectType
        }

        switch (schemaInfo.getXJavaType ()) {
            case Map.name:
                objectType = new MapDataType ()
                dataTypes.add (schemaInfo.name, objectType)
                break

            default:
                objectType = new ObjectDataType (
                    type: schemaInfo.name,
                    pkg: [options.packageName, 'model'].join ('.')
                )

                schemaInfo.eachProperty { String propName, SchemaInfo propDataTypeInfo ->
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

        simpleType
    }


    private String getObjectDataType(SchemaInfo schemaInfo) {
        if (options.typeMappings) {

            List<EndpointTypeMapping> endpoints = getEndpointMappings ()

            if (schemaInfo instanceof ResponseSchemaInfo) {
                String ep = schemaInfo.path
                String ct = schemaInfo.contentType

                // check endpoint response mapping
                EndpointTypeMapping endpoint = endpoints.find { it.path == ep }
                if (endpoint) {
                    List<ResponseTypeMapping> responses = getResponseMappings (endpoint.typeMappings)

                    def response = responses.find { it.contentType == ct && it.sourceTypeName == 'object' }
                    if (response) {
                        return response.targetTypeName
                    }
                }
            }

            // check global mapping
            List<TypeMapping> mappings = options.typeMappings.findResults {
                it instanceof TypeMapping && it.sourceTypeName == schemaInfo.name ? it : null
            }

            if (mappings.isEmpty ()) {
                return null
            }

            if (mappings.size () != 1) {
                // todo throw new DuplicateTypeMapping();
            }

            def mapping = mappings.first ()
            return mapping.targetTypeName
        }

        null
    }

    private String getArrayDataType(SchemaInfo schemaInfo) {
        if (options.typeMappings) {

            List<EndpointTypeMapping> endpoints = getEndpointMappings ()

            if (schemaInfo instanceof ResponseSchemaInfo) {
                String ep = schemaInfo.path
                String ct = schemaInfo.contentType

                // check endpoint response mapping
                EndpointTypeMapping endpoint = endpoints.find { it.path == ep }
                if (endpoint) {
                    List<ResponseTypeMapping> responses = getResponseMappings (endpoint.typeMappings)

                    def response = responses.find { it.contentType == ct && it.sourceTypeName == 'array' }
                    if (response) {
                        return response.targetTypeName
                    }
                }

                // check global response mapping
                List<ResponseTypeMapping> responses = getResponseMappings (options.typeMappings)
                def response = responses.find { it.contentType == ct && it.sourceTypeName == 'array' }
                if (response) {
                    return response.targetTypeName
                }
            }

            // check global mapping
            List<TypeMapping> arrays = options.typeMappings.findResults {
                it instanceof TypeMapping && it.sourceTypeName == 'array' ? it : null
            }

            // no mapping, use default
            if (arrays.isEmpty ()) {
                return null
            }

            if (arrays.size () != 1) {
                // todo throw new DuplicateTypeMapping();
            }

            def array = arrays.first ()
            return array.targetTypeName
        }

        null
    }

    private List<ResponseTypeMapping> getResponseMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof ResponseTypeMapping ? it : null
        }
    }

    private List<EndpointTypeMapping> getEndpointMappings () {
        options.typeMappings.findResults {
            it instanceof EndpointTypeMapping ? it : null
        }
    }

}
