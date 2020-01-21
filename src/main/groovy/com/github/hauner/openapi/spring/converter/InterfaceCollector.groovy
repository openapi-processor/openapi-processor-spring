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
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.converter.mapping.MappingSchema
import com.github.hauner.openapi.spring.model.Interface
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths

/**
 * Collects the target interfaces for the endpoints of the OpenAPI description.
 *
 * @author Martin Hauner
 */
class InterfaceCollector {

    public static final String INTERFACE_DEFAULT_NAME = ''

    private ApiOptions options

    class MappingSchemaEndpoint implements MappingSchema {
        String path

        @Override
        String getPath () {
            path
        }

        @Override
        String getName () {
            null
        }

        @Override
        String getContentType () {
            null
        }
    }

    InterfaceCollector(ApiOptions options) {
        this.options = options
    }

    /**
     * extracts the interface names used to group the endpoints from the open api specification.
     * The first tag of an operation (i.e. endpoint) is used as the name of the target interface.
     *
     * @param paths
     * @return list of interfaces to generate
     */
    List<Interface> collect(Paths paths) {
        Map<String, Interface> interfaces = new HashMap<> ()

        paths.each { Map.Entry<String, PathItem> entry ->
            def operations = collectOperations (entry.value)
            operations.each { op ->
                String targetInterfaceName = getInterfaceName (op, isExcluded (entry.key))

                if (interfaces.containsKey (targetInterfaceName)) {
                    return
                }

                def itf = new Interface (
                    pkg: [options.packageName, 'api'].join ('.'),
                    name: targetInterfaceName
                )

                interfaces.put (targetInterfaceName, itf)
            }
        }

        interfaces.values () as List
    }

    private boolean isExcluded (String path) {
        def endpointMatches = options.typeMappings.findAll {
            it.matches (Mapping.Level.ENDPOINT, new MappingSchemaEndpoint(path: path))
        }

        if (!endpointMatches.empty) {
            if (endpointMatches.size () != 1) {
                throw new AmbiguousTypeMappingException (endpointMatches)
            }

            def match = endpointMatches.first () as EndpointTypeMapping
            return match.exclude
        }

        false
    }

    private List<Operation> collectOperations(PathItem item) {
        new OperationCollector ().collect (item)
    }

    private String getInterfaceName (def op, boolean excluded) {
        String targetInterfaceName = INTERFACE_DEFAULT_NAME

        if (hasTags (op)) {
            targetInterfaceName = op.tags.first ()
        }

        if (excluded) {
            targetInterfaceName += 'Excluded'
        }

        targetInterfaceName
    }

    private boolean hasTags (op) {
        op.tags && !op.tags.empty
    }

}
