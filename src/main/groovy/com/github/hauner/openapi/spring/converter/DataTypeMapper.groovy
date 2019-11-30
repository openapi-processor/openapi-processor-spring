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

import com.github.hauner.openapi.spring.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.spring.converter.mapping.TargetType
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMappingX
import com.github.hauner.openapi.spring.converter.schema.SchemaType

/**
 * Checks if there is a mapping for a given type. Used by DataTypeConverter.
 *
 * @author Martin Hauner
 */
class DataTypeMapper {

    private List<TypeMappingX> typeMappings

    DataTypeMapper(List<?> typeMappings) {
        this.typeMappings = (typeMappings ?: []) as List<TypeMappingX>
    }

    TargetType getMappedDataType (SchemaType schemaType) {

        // check endpoint mappings
        List<TypeMappingX> endpointMatches = schemaType.matchEndpointMapping (typeMappings)
        if (!endpointMatches.empty) {
            TargetType target = endpointMatches.first().targetType
            if (target) {
                return target
            }
        }

        // check global parameter & response mappings
        List<TypeMappingX> ioMatches = schemaType.matchIoMapping (typeMappings)
        if (!ioMatches.empty) {
            TargetType target = ioMatches.first().targetType
            if (target) {
                return target
            }
        }

        // check global type mapping
        List<TypeMappingX> typeMatches = schemaType.matchTypeMapping (typeMappings)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size () != 1) {
            throw new AmbiguousTypeMappingException (typeMatches)
        }

        TypeMapping match = typeMatches.first () as TypeMapping
        return match.targetType
    }

}
