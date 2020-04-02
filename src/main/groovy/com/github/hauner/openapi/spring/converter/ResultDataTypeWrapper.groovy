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

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.converter.mapping.AmbiguousTypeMappingException
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.converter.mapping.TargetType
import com.github.hauner.openapi.spring.converter.mapping.TargetTypeMapping
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.spring.model.datatypes.ResultDataType

/**
 * wraps the result data type with the mapped result type.
 *
 * @author Martin Hauner
 */
class ResultDataTypeWrapper {

    private ApiOptions options
    private MappingFinder finder

    ResultDataTypeWrapper (ApiOptions options) {
        this.options = options
        this.finder = new MappingFinder(typeMappings: options.typeMappings)
    }

    /**
     * wraps a (converted) result data type with the configured result java data type like
     * {@code ResponseEntity}, {@code Flux} etc.
     *
     * If the configuration for the result type is 'plain' the source data type is not wrapped.
     *
     * @param dataType the data type to wrap
     * @param schemaInfo the open api type with context information
     * @return the resulting java data type
     */
    DataType wrap (DataType dataType, SchemaInfo schemaInfo) {
        TargetType targetType = getMappedResultDataType (schemaInfo)

        if (!targetType) {
            return dataType
        }

        if (targetType.typeName == 'plain') {
            return dataType

        } else if (targetType.typeName == 'mvc') {
            def resultType = new ResultDataType (
                type: 'ResponseEntity',
                pkg: 'org.springframework.http',
                dataType: dataType
            )
            return resultType
            
        } else {
            def resultType = new ResultDataType (
                type: targetType.name,
                pkg: targetType.pkg,
                dataType: dataType
            )
            return resultType
        }
    }

    private TargetType getMappedResultDataType (SchemaInfo info) {
        // check endpoint result mapping
        List<Mapping> endpointMatches = finder.findEndpointResultMapping (info)

        if (!endpointMatches.empty) {

            if (endpointMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (endpointMatches)
            }

            TargetType target = (endpointMatches.first() as TargetTypeMapping).targetType
            if (target) {
                return target
            }
        }

        // find global result mapping
        List<Mapping> typeMatches = finder.findResultMapping (info)
        if (typeMatches.isEmpty ()) {
            return null
        }

        if (typeMatches.size () != 1) {
            throw new AmbiguousTypeMappingException (typeMatches)
        }

        def match = typeMatches.first () as TargetTypeMapping
        return match.targetType
    }

}
