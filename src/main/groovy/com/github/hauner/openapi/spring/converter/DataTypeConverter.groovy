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
import com.github.hauner.openapi.core.converter.mapping.TargetType
import com.github.hauner.openapi.core.converter.mapping.TargetTypeMapping
import com.github.hauner.openapi.core.converter.mapping.Mapping
import com.github.hauner.openapi.spring.model.DataTypes
import com.github.hauner.openapi.spring.model.datatypes.ArrayDataType
import com.github.hauner.openapi.spring.model.datatypes.BooleanDataType
import com.github.hauner.openapi.spring.model.datatypes.ComposedObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataTypeConstraints
import com.github.hauner.openapi.spring.model.datatypes.LocalDateDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedCollectionDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedDataType
import com.github.hauner.openapi.spring.model.datatypes.MappedMapDataType
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.DoubleDataType
import com.github.hauner.openapi.spring.model.datatypes.FloatDataType
import com.github.hauner.openapi.spring.model.datatypes.IntegerDataType
import com.github.hauner.openapi.spring.model.datatypes.LongDataType
import com.github.hauner.openapi.spring.model.datatypes.OffsetDateTimeDataType
import com.github.hauner.openapi.spring.model.datatypes.LazyDataType
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
    private MappingFinder finder

    private List<SchemaInfo> current


    DataTypeConverter(ApiOptions options) {
        this.options = options
        this.finder = new MappingFinder(typeMappings: options.typeMappings)
        this.current = []
    }

    /**
     * converts an open api type (i.e. a {@code Schema}) to a java data type including nested types.
     * Stores named objects in {@code dataTypes} for re-use. {@code dataTypeInfo} provides the type
     * name used to add it to the list of data types.
     *
     * @param schemaInfo the open api type with context information
     * @param dataTypes known object types
     * @return the resulting java data type
     */
    DataType convert (SchemaInfo schemaInfo, DataTypes dataTypes) {
        if (isLoop (schemaInfo)) {
            return new LazyDataType (info: schemaInfo, dataTypes: dataTypes)
        }

        push (schemaInfo)

        DataType result
        if (schemaInfo.isRefObject ()) {
            result = createRefDataType (schemaInfo, dataTypes)

        } else if (schemaInfo.isComposedObject ()) {
            result = createComposedDataType (schemaInfo, dataTypes)

        } else if (schemaInfo.isArray ()) {
            result = createArrayDataType (schemaInfo, dataTypes)

        } else if (schemaInfo.isObject ()) {
            result = createObjectDataType (schemaInfo, dataTypes)

        } else {
            result = createSimpleDataType (schemaInfo, dataTypes)
        }

        pop ()
        result
    }

    private DataType createComposedDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        def objectType

        TargetType targetType = getMappedDataType (schemaInfo)
        if (targetType) {
            objectType = new MappedDataType (
                type: targetType.name,
                pkg: targetType.pkg,
                genericTypes: targetType.genericNames
            )
            return objectType
        }

        objectType = new ComposedObjectDataType (
            type: schemaInfo.name,
            pkg: [options.packageName, 'model'].join ('.'),
            of: schemaInfo.itemOf ()
        )

        schemaInfo.eachItemOf { SchemaInfo itemSchemaInfo ->
            def itemType = convert (itemSchemaInfo, dataTypes)
            objectType.addItems (itemType)
        }

        dataTypes.add (objectType)
        objectType
    }

    private DataType createArrayDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        SchemaInfo itemSchemaInfo = schemaInfo.buildForItem ()
        DataType item = convert (itemSchemaInfo, dataTypes)

        TargetType targetType = getMappedDataType (schemaInfo)

        def constraints = new DataTypeConstraints(
            defaultValue: schemaInfo.defaultValue,
            nullable: schemaInfo.nullable,
            minItems: schemaInfo.minItems,
            maxItems: schemaInfo.maxItems,
        )

        if (targetType) {
            def mappedDataType = new MappedCollectionDataType (
                type: targetType.name,
                pkg: targetType.pkg,
                item: item
            )
            return mappedDataType
        }

        new ArrayDataType (item: item, constraints: constraints)
    }

    private DataType createRefDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        convert (schemaInfo.buildForRef (), dataTypes)
    }

    private DataType createObjectDataType (SchemaInfo schemaInfo, DataTypes dataTypes) {
        def objectType

        TargetType targetType = getMappedDataType (schemaInfo)
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
            nullable: schemaInfo.nullable
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

        TargetType targetType = getMappedDataType (schemaInfo)
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
                throw new UnknownDataTypeException(schemaInfo.name, schemaInfo.type, schemaInfo.format)
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

    private TargetType getMappedDataType (SchemaInfo info) {
        // check endpoint mappings
        List<Mapping> endpointMatches = finder.findEndpointMappings (info)

        if (!endpointMatches.empty) {

            if (endpointMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (endpointMatches)
            }

            TargetType target = (endpointMatches.first() as TargetTypeMapping).targetType
            if (target) {
                return target
            }
        }

        // check global io (parameter & response) mappings
        List<Mapping> ioMatches = finder.findIoMappings (info)
        if (!ioMatches.empty) {

            if (ioMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (ioMatches)
            }

            TargetType target = (ioMatches.first() as TargetTypeMapping).targetType
            if (target) {
                return target
            }
        }

        // check global type mapping
        List<Mapping> typeMatches = finder.findTypeMappings (info)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size () != 1) {
            throw new AmbiguousTypeMappingException (typeMatches)
        }

        def match = typeMatches.first () as TargetTypeMapping
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
