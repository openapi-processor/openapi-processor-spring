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
 * Used with {@link com.github.hauner.openapi.spring.converter.ApiOptions#typeMappings} to map an
 * OpenAPI schema to a java type.
 *
 * To override the type mapping of the OpenAPI {@code array} from a simple java array to another
 * collection type the @(link #soucrceTypeName) should be set to {@code array}.
 *
 * @author Martin Hauner
 */
class TypeMapping implements Mapping {

    /**
     * The OpenAPI schema type that should be mapped to the {@link #targetTypeName} java type.
     */
    String sourceTypeName

    /**
     * The OpenAPI format of {@link #sourceTypeName} that should be mapped to the
     * {@link #targetTypeName} java type.
     */
    String sourceTypeFormat

    /**
     * The fully qualified java type name that will replace {@link #sourceTypeName}.
     */
    String targetTypeName

    /**
     * The fully qualified java type names of all generic parameters to {@link #targetTypeName}.
     */
    List<String> genericTypeNames = []

    /**
     * Returns the full source type as {@link #sourceTypeName} and {@link #sourceTypeFormat} joined
     * by a ':' separator.
     *
     * @return the full source type
     */
    String getFullSourceType () {
        "$sourceTypeName" + (sourceTypeFormat ? ":$sourceTypeFormat" : "")
    }

    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    TargetType getTargetType () {
        new TargetType (typeName: targetTypeName, genericNames: genericTypeNames)
    }

    @Override
    boolean matches (Level level, MappingSchema schema) {
        Level.TYPE == level && sourceTypeName == schema.name
    }

    @Override
    boolean matches (Level level, MappingSchemaType schemaType) {
        Level.TYPE == level &&
            sourceTypeName == schemaType.type && sourceTypeFormat == schemaType.format
    }

    @Override
    List<Mapping> getChildMappings () {
        [this]
    }

}
