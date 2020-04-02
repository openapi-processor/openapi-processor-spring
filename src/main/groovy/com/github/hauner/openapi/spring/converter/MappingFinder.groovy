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

import com.github.hauner.openapi.spring.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.converter.mapping.MappingVisitor
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResultTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.schema.SchemaInfo


/**
 * find mapping in type mapping list for a schema info.
 * 
 * @author Martin Hauner
 */
class MappingFinder {

    private List<Mapping> typeMappings = []


    class BaseVisitor implements MappingVisitor {
        SchemaInfo schemaInfo

        @Override
        boolean match (EndpointTypeMapping mapping) {
            false
        }

        @Override
        boolean match (ParameterTypeMapping mapping) {
            false
        }

        @Override
        boolean match (ResponseTypeMapping mapping) {
            false
        }

        @Override
        boolean match (TypeMapping mapping) {
            false
        }

        @Override
        boolean match (AddParameterTypeMapping mapping) {
            false
        }

        @Override
        boolean match (ResultTypeMapping mapping) {
            false
        }

    }

    class EndpointMatcher extends BaseVisitor {

        @Override
        boolean match (EndpointTypeMapping mapping) {
            mapping.path == schemaInfo.path
        }

    }

    class IoMatcher extends BaseVisitor {

        @Override
        boolean match (ParameterTypeMapping mapping) {
            mapping.parameterName == schemaInfo.name
        }

        @Override
        boolean match (ResponseTypeMapping mapping) {
            mapping.contentType == schemaInfo.contentType
        }

    }

    class TypeMatcher extends BaseVisitor {

        @Override
        boolean match (TypeMapping mapping) {
            if (schemaInfo.isPrimitive ()) {
                mapping.sourceTypeName == schemaInfo.type && mapping.sourceTypeFormat == schemaInfo.format

            } else if (schemaInfo.isArray ()) {
                mapping.sourceTypeName == 'array'

            } else {
                mapping.sourceTypeName == schemaInfo.name
            }
        }

    }
    
    /**
     * find any matching endpoint mapping for the given schema info.
     *
     * @param info schema info of the OpenAPI schema.
     * @return list of matching mappings
     */
    List<Mapping> findEndpointMappings (SchemaInfo info) {
        List<Mapping> ep = filterMappings (new EndpointMatcher (schemaInfo: info), typeMappings)

        List<Mapping> io = filterMappings (new IoMatcher (schemaInfo: info), ep)
        if (!io.empty) {
            return io
        }

        filterMappings (new TypeMatcher (schemaInfo: info), ep)
    }

    private List<Mapping> filterMappings (MappingVisitor visitor, List<Mapping> mappings) {
        mappings
            .findAll {
                it.matches (visitor)
            }
            .collectMany {
                it.childMappings
            }
    }
    
}
