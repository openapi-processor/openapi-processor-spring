/*
 * Copyright 2019-2020 the original authors
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

import com.github.hauner.openapi.spring.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.spring.converter.mapping.TargetType
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.converter.schema.ArraySchemaType
import com.github.hauner.openapi.spring.converter.schema.ObjectSchemaType
import com.github.hauner.openapi.spring.converter.schema.PrimitiveSchemaType
import com.github.hauner.openapi.spring.converter.schema.SchemaInfo
import com.github.hauner.openapi.spring.converter.schema.SchemaType
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.CollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.spring.model.datatypes.ListDataType
import com.github.hauner.openapi.spring.model.datatypes.LocalDateDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType
import com.github.hauner.openapi.spring.model.datatypes.OffsetDateTimeDataType
import com.github.hauner.openapi.spring.model.datatypes.LazyDataType
import com.github.hauner.openapi.spring.model.datatypes.SetDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType

/**
 * Converter to map OpenAPI schemas to Java data types.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class DataTypeConverter {

    private ApiOptions options
    private List<SchemaInfo> current


    DataTypeConverter(ApiOptions options) {
        this.options = options
        this.current = []
    }

    DataType none() {
        new NoneDataType()
    }

    /**
     * converts an open api type (i.e. a {@code Schema}) to a java data type including nested types.
     * Stores named objects in {@code dataTypes} for re-use. {@code dataTypeInfo} provides the type
     * name used to add it to the list of data types.
     *
     * @param dataTypeInfo the open api type with context information
     * @param dataTypes known object types
     * @return the resulting java data type
     */
    DataType convert (SchemaInfo dataTypeInfo, DataTypes dataTypes) {
        if (isLoop (dataTypeInfo)) {
            return new LazyDataType (info: dataTypeInfo, dataTypes: dataTypes)
        }

        push (dataTypeInfo)

        DataType result
        if (dataTypeInfo.isRefObject ()) {
            result = createRefDataType(dataTypeInfo, dataTypes)

        } else if (dataTypeInfo.isArray ()) {
            result = createArrayDataType (dataTypeInfo, dataTypes)

        } else if (dataTypeInfo.isObject ()) {
            result = createObjectDataType (dataTypeInfo, dataTypes)

        } else {
            result = createSimpleDataType (dataTypeInfo, dataTypes)
        }

        pop ()
        result
    }

    private DataType createArrayDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        SchemaInfo itemSchemaInfo = schemaInfo.buildForItem ()
        DataType item = convert (itemSchemaInfo, dataTypes)

        def arrayType
        TargetType targetType = getMappedDataType (new ArraySchemaType (schemaInfo))

        def constraints = new DataTypeConstraints(
            defaultValue: schemaInfo.defaultValue,
            nullable: schemaInfo.nullable,
            minItems: schemaInfo.minItems,
            maxItems: schemaInfo.maxItems,
        )

        switch (targetType?.typeName) {
            case Collection.name:
                arrayType = new CollectionDataType (item: item, constraints: constraints)
                break
            case List.name:
                arrayType = new ListDataType (item: item, constraints: constraints)
                break
            case Set.name:
                arrayType = new SetDataType (item: item, constraints: constraints)
                break
            default:
                arrayType = new ArrayDataType (item: item, constraints: constraints)
        }

        arrayType
    }

    private DataType createRefDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        convert (schemaInfo.buildForRef (), dataTypes)
    }

    private DataType createObjectDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        def objectType

        TargetType targetType = getMappedDataType (new ObjectSchemaType (schemaInfo))
        if (targetType) {
            switch (targetType?.typeName) {
                case Map.name:
                case 'org.springframework.util.MultiValueMap':
                    objectType = new MappedMapDataType (
                        type: targetType.name,
                        pkg: targetType.pkg,
                        genericTypes: targetType.genericNames
                    )
                    return objectType
                default:
                    objectType = new MappedDataType (
                        type: targetType.name,
                        pkg: targetType.pkg,
                        genericTypes: targetType.genericNames
                    )

                    // probably not required anymore
                    dataTypes.add (schemaInfo.name, objectType)
                    return objectType
            }
        }

        def constraints = new DataTypeConstraints(
            nullable: schemaInfo.nullable,
        )

        objectType = new ObjectDataType (
            type: schemaInfo.name,
            pkg: [options.packageName, 'model'].join ('.'),
            constraints: constraints
        )

        schemaInfo.eachProperty { String propName, SchemaInfo propDataTypeInfo ->
            def propType = convert (propDataTypeInfo, dataTypes)
            objectType.addObjectProperty (propName, propType)
        }

        dataTypes.add (objectType)
        objectType
    }

    private DataType createSimpleDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {

        TargetType targetType = getMappedDataType (new PrimitiveSchemaType(schemaInfo))
        if (targetType) {
            def simpleType = new MappedDataType (
                type: targetType.name,
                pkg: targetType.pkg,
                genericTypes: targetType.genericNames
            )
            return simpleType
        }

        def typeFormat = schemaInfo.type
        if (schemaInfo.format) {
            typeFormat += '/' + schemaInfo.format
        }

        def constraints = new DataTypeConstraints(
            defaultValue: schemaInfo.defaultValue,
            nullable: schemaInfo.nullable,
            minLength: schemaInfo.minLength,
            maxLength: schemaInfo.maxLength,
            maximum: schemaInfo.maximum,
            exclusiveMaximum: schemaInfo.exclusiveMaximum,
            minimum: schemaInfo.minimum,
            exclusiveMinimum: schemaInfo.exclusiveMinimum,
        )

        def simpleType
        switch (typeFormat) {
            case 'integer':
            case 'integer/int32':
                simpleType = new IntegerDataType (constraints: constraints)
                break
            case 'integer/int64':
                simpleType = new LongDataType (constraints: constraints)
                break
            case 'number':
            case 'number/float':
                simpleType = new FloatDataType (constraints: constraints)
                break
            case 'number/double':
                simpleType = new DoubleDataType (constraints: constraints)
                break
            case 'boolean':
                simpleType = new BooleanDataType (constraints: constraints)
                break
            case 'string':
                simpleType = createStringDataType (schemaInfo, constraints, dataTypes)
                break
            case 'string/date':
                simpleType = new LocalDateDataType ()
                break
            case 'string/date-time':
                simpleType = new OffsetDateTimeDataType ()
                break
            default:
                throw new UnknownDataTypeException(schemaInfo.type, schemaInfo.format)
        }

        simpleType
    }

    private DataType createStringDataType (SchemaInfo info, DataTypeConstraints constraints, DataTypes dataTypes) {
        if (!info.isEnum()) {
            return new StringDataType (constraints: constraints)
        }

        // in case of an inline definition the name may be lowercase, make sure the enum
        // class gets an uppercase name!
        def enumType = new StringEnumDataType (
            type: info.name.capitalize (),
            pkg: [options.packageName, 'model'].join ('.'),
            values: info.enumValues,
            constraints: constraints)

        dataTypes.add (enumType)
        enumType
    }

    private TargetType getMappedDataType (SchemaType schemaType) {
        // check endpoint mappings
        List<Mapping> endpointMatches = schemaType.matchEndpointMapping (options.typeMappings)
        if (!endpointMatches.empty) {

            if (endpointMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (endpointMatches)
            }

            TargetType target = (endpointMatches.first() as TypeMapping).targetType
            if (target) {
                return target
            }
        }

        // check global io (parameter & response) mappings
        List<Mapping> ioMatches = schemaType.matchIoMapping (options.typeMappings)
        if (!ioMatches.empty) {

            if (ioMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (ioMatches)
            }

            TargetType target = (ioMatches.first() as TypeMapping).targetType
            if (target) {
                return target
            }
        }

        // check global type mapping
        List<Mapping> typeMatches = schemaType.matchTypeMapping (options.typeMappings)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size () != 1) {
            throw new AmbiguousTypeMappingException (typeMatches)
        }

        TypeMapping match = typeMatches.first () as TypeMapping
        return match.targetType
    }

    /**
     * push the current schema info.
     *
     * Pushes the given {@code info} onto the in-progress data type stack. It is used to detect
     * $ref loops.
     *
     * @param info the schema info that is currently processed
     */
    private void push (SchemaInfo info) {
        current.push (info)
    }

    /**
     * pop the current schema info.
     *
     */
    private void pop () {
        current.pop ()
    }

    /**
     * detect $ref loop.
     *
     * returns true if the given {@code info} is currently processed, false otherwise. True indicates
     * a $ref loop.
     *
     * @param info the schema info that is currently processed
     * @return
     */
    private boolean isLoop (SchemaInfo info) {
        def found = current.find {
            it.name == info.name
        }
        found != null
    }

}
