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

    TargetType getMappedObjectDataType (SchemaInfo schemaInfo) {

        if (schemaInfo instanceof ResponseSchemaInfo) {
            String ct = schemaInfo.contentType

            // check endpoint response mapping
            EndpointTypeMapping endpoint = getEndpointMappings ().find { it.matches (schemaInfo) }
            if (endpoint) {
                TypeMapping mapping = endpoint.findMatch (schemaInfo)
                if (mapping) {
                    return mapping.targetType
                }
            }

            // check global response mapping
            List<ResponseTypeMapping> responses = getResponseMappings (typeMappings)
            def response = responses.find { it.contentType == ct && it.mapping.sourceTypeName == 'object' }
            if (response) {
                return new TargetType (
                    typeName: response.mapping.targetTypeName,
                    genericNames: response.mapping.genericTypeNames
                )
            }
        }

        if (schemaInfo instanceof ParameterSchemaInfo) {
            String pn = schemaInfo.name

            // check endpoint parameter mapping
            EndpointTypeMapping endpoint = getEndpointMappings ().find { it.path == schemaInfo.path }
            if (endpoint) {
                List<ParameterTypeMapping> parameters = getParameterMappings (endpoint.typeMappings)

                def parameter = parameters.find { it.parameterName == pn && it.mapping.sourceTypeName == 'object' }
                if (parameter) {
                    return new TargetType (
                        typeName: parameter.mapping.targetTypeName,
                        genericNames: parameter.mapping.genericTypeNames
                    )
                }
            }

            // check global parameter mapping
            List<ParameterTypeMapping> parameters = getParameterMappings (typeMappings)
            def parameter = parameters.find { it.parameterName == pn && it.mapping.sourceTypeName == 'object' }
            if (parameter) {
                return new TargetType (
                    typeName: parameter.mapping.targetTypeName,
                    genericNames: parameter.mapping.genericTypeNames
                )
            }
        }

        // check endpoint type mapping
        EndpointTypeMapping endpoint = getEndpointMappings ().find { it.path == schemaInfo.path }
        if (endpoint) {
            List<TypeMapping> mappings = getTypeMappings (endpoint.typeMappings)

            def mapping = mappings.find { it.sourceTypeName == schemaInfo.name ? it : null }
            if (mapping) {
                return new TargetType (
                    typeName: mapping.targetTypeName,
                    genericNames: mapping.genericTypeNames
                )
            }
        }

        // check global mapping
        List<TypeMapping> matches = typeMappings.findResults {
            it instanceof TypeMapping && it.sourceTypeName == schemaInfo.name ? it : null
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

    private List<ParameterTypeMapping> getParameterMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof ParameterTypeMapping ? it : null
        }
    }

    private List<ResponseTypeMapping> getResponseMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof ResponseTypeMapping ? it : null
        }
    }

    private List<EndpointTypeMapping> getEndpointMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof EndpointTypeMapping ? it : null
        }
    }

    private List<EndpointTypeMapping> getEndpointMappings () {
        getEndpointMappings (typeMappings)
    }

    private List<EndpointTypeMapping> getEndpointMappings (SchemaInfo info) {
        getEndpointMappings (typeMappings)
    }

    private List<TypeMapping> getTypeMappings (List<?> typeMappings) {
        typeMappings.findResults {
            it instanceof TypeMapping ? it : null
        }
    }

    private List<TypeMapping> getTypeMappings () {
        options.typeMappings.findResults {
            it instanceof TypeMapping ? it : null
        }
    }

}
