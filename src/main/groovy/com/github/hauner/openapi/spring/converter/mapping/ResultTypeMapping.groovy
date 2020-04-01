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

package com.github.hauner.openapi.spring.converter.mapping

/**
 * Used with {@link com.github.hauner.openapi.spring.converter.ApiOptions#typeMappings} to map an
 * OpenAPI response to a plain java type or to a wrapper type of the plain type.
 *
 * @author Martin Hauner
 */
class ResultTypeMapping implements Mapping {

    /**
     * The fully qualified java type name that will be used as the result type.
     */
    String targetTypeName

    /**
     * The fully qualified java type names of all generic parameters to {@link #targetTypeName}.
     */
    List<String> genericTypeNames = []
    
    @Override
    boolean matches (Level level, MappingSchema schema) {
        Level.RESULT == level
    }

    @Override
    boolean matches (Level level, MappingSchemaType schemaType) {
        Level.RESULT == level
    }

    @Override
    List<Mapping> getChildMappings () {
        [this]
    }

}
