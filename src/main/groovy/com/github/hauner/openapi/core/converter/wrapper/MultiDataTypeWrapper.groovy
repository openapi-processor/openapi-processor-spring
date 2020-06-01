/*
 * Copyright 2020 the original authors
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

package com.github.hauner.openapi.core.converter.wrapper

import com.github.hauner.openapi.core.converter.ApiOptions
import com.github.hauner.openapi.core.converter.mapping.MappingFinder
import com.github.hauner.openapi.core.converter.SchemaInfo
import com.github.hauner.openapi.core.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.core.converter.mapping.Mapping
import com.github.hauner.openapi.core.converter.mapping.TargetType
import com.github.hauner.openapi.core.converter.mapping.TargetTypeMapping
import com.github.hauner.openapi.core.model.datatypes.DataType
import com.github.hauner.openapi.core.model.datatypes.MappedCollectionDataType
import com.github.hauner.openapi.core.model.datatypes.NoneDataType

/**
 * replaces a collection wrapper with the 'multi' data mapping.
 *
 * Used to replace the collection wrapper at Responses or RequestBody's with  {@code Flux<>} or
 * similar types.
 *
 * @author Martin Hauner
 */
class MultiDataTypeWrapper {

    private ApiOptions options
    private MappingFinder finder

    MultiDataTypeWrapper (ApiOptions options) {
        this.options = options
        this.finder = new MappingFinder(typeMappings: options.typeMappings)
    }

    /**
     * replaces an (converted) array data type with a multi data type (like {@code Flux< >})
     * wrapping the collection item.
     *
     * If the configuration for the result type is 'plain' or not defined the source data type
     * is not changed.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    DataType wrap (DataType dataType, SchemaInfo schemaInfo) {
        if (!schemaInfo.isArray ()) {
            return dataType
        }

        def targetType = getMultiDataType (schemaInfo)
        if (!targetType) {
            return dataType
        }

        if (targetType.typeName == 'plain') {
            return dataType
        }

        DataType item = dataType.item

        def multiType = new MappedCollectionDataType (
            type: targetType.name,
            pkg: targetType.pkg,
            item: item
        )
        return multiType
    }

    private TargetType getMultiDataType (SchemaInfo info) {
        // check endpoint multi mapping
        List<Mapping> endpointMatches = finder.findEndpointMultiMapping (info)

        if (!endpointMatches.empty) {

            if (endpointMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (endpointMatches)
            }

            TargetType target = (endpointMatches.first() as TargetTypeMapping).targetType
            if (target) {
                return target
            }
        }

        // find global multi mapping
        List<Mapping> typeMatches = finder.findMultiMapping (info)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size () != 1) {
            throw new AmbiguousTypeMappingException (typeMatches)
        }

        def match = typeMatches.first () as TargetTypeMapping
        return match.targetType
    }

    private DataType checkNone (DataType dataType) {
        if (dataType instanceof NoneDataType) {
            return dataType.wrappedInResult ()
        }

        dataType
    }

}
