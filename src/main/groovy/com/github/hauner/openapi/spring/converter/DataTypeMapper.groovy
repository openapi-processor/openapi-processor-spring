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

    TargetType getMappedDataType (SchemaInfo schemaInfo, String type) {

        // check endpoint mappings
        List<EndpointTypeMapping> endpoints = getEndpointMappings (schemaInfo)
        if (!endpoints.empty) {
            List<TypeMapping> matches = endpoints.first ().findMatches (schemaInfo)
            if(!matches.empty) {
                TargetType target = matches.first().targetType
                if (target) {
                    return target
                }
            }
        }

        // check global parameter & response mappings
        List<?> matchesX = getGlobalMappings (schemaInfo)
        if (!matchesX.empty) {
            TargetType target = matchesX.first().mapping.targetType
            if (target) {
                return target
            }
        }

        // check global type mappings
        List<TypeMapping> matches = []
        switch(type) {
            case 'array':
                matches = typeMappings.findResults {
                    it instanceof TypeMapping && it.sourceTypeName == 'array' ? it : null
                }
                break
            case 'object':
                matches = typeMappings.findResults {
                    it instanceof TypeMapping && it.sourceTypeName == schemaInfo.name ? it : null
                }
                break
        }

        if (matches.isEmpty ()) {
            return null
        }

        if (matches.size () != 1) {
            throw new AmbiguousTypeMappingException (matches)
        }

        def match = matches.first ()
        return new TargetType (
            typeName: match.targetTypeName,
            genericNames: match.genericTypeNames ?: [])
    }

    private List<?> getGlobalMappings (SchemaInfo info) {
        typeMappings.findResults {
            it instanceof ParameterTypeMapping || it instanceof ResponseTypeMapping ? it : null
        }.findAll {
            it.matches (info)
        }
    }

    private List<EndpointTypeMapping> getEndpointMappings (SchemaInfo info) {
        getEndpointMappings (typeMappings).findAll { it.matches (info) }
    }

    private List<EndpointTypeMapping> getEndpointMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof EndpointTypeMapping ? it : null
        }
    }

    private List<TypeMapping> getTypeMappings () {
        options.typeMappings.findResults {
            it instanceof TypeMapping ? it : null
        }
    }

}
