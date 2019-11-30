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
import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TargetType
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.schema.SchemaType

/**
 * Checks if there is a mapping for a given type. Used by DataTypeConverter.
 *
 * @author Martin Hauner
 */
class DataTypeMapper {

    private List<?> typeMappings

    DataTypeMapper(List<?> typeMappings) {
        this.typeMappings = typeMappings ?: []
    }

    TargetType getMappedDataType (SchemaType schemaType) {

        // check endpoint mappings
        List<?> matchesW = schemaType.findEndpointMappings (getEndpointMappings ())
        if (!matchesW.empty) {
            TargetType target = matchesW.first().targetType
            if (target) {
                return target
            }
        }

        // check global parameter & response mappings
        List<?> matchesX = schemaType.findGlobalMappings (getGlobalMappings ())
        if (!matchesX.empty) {
            TargetType target = matchesX.first().targetType
            if (target) {
                return target
            }
        }

        // check global type mapping
        List<?> matches = schemaType.findGlobalTypeMappings (getGlobalTypeMappings ())
        if (matches.isEmpty ()) {
            return null
        }

        if (matches.size () != 1) {
            throw new AmbiguousTypeMappingException (matches)
        }

        TypeMapping match = matches.first () as TypeMapping
        return match.targetType
    }

    private List<EndpointTypeMapping> getEndpointMappings() {
        getEndpointMappings (typeMappings)
    }

    private List<?> getGlobalMappings () {
        typeMappings.findResults {
            it instanceof ParameterTypeMapping || it instanceof ResponseTypeMapping ? it : null
        }
    }

    private List<EndpointTypeMapping> getEndpointMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof EndpointTypeMapping ? it : null
        }
    }

    private List<TypeMapping> getGlobalTypeMappings () {
        typeMappings.findResults {
            it instanceof TypeMapping ? it : null
        }
    }

}
