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

package com.github.hauner.openapi.spring.converter.mapping
/**
 * Used with {@link com.github.hauner.openapi.spring.converter.ApiOptions} to override parameter or
 * response type mappings on a single endpoint. It can also be used to add parameters that are not
 * defined in the api. For example to pass {@code javax.servlet.http.HttpServletRequest} to the
 * controller method.
 *
 * The {@code mappings} list can contain objects of the type
 * - {@link ParameterTypeMapping}
 * - {@link ResponseTypeMapping}
 *
 * @author Martin Hauner
 */
class EndpointTypeMapping implements Mapping {

    /**
     * Full path of the endpoint that is configured by this object.
     */
    String path

    /**
     * Exclude endpoint.
     */
    boolean exclude

    /**
     * Provides type mappings for the endpoint. The list can contain the following mappings:
     *
     * {@link ResponseTypeMapping}: used to map a response schema type to a java type.
     */
    List<Mapping> typeMappings

    @Override
    boolean matches (Level level, MappingSchema schema) {
        Level.ENDPOINT == level && path == schema.path
    }

    @Override
    boolean matches (Level level, MappingSchemaType schemaType) {
        false
    }

    @Override
    List<Mapping> getChildMappings () {
        typeMappings
    }

}
