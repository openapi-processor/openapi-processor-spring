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
 * Common interface for type mappings.
 */
interface Mapping {
    // mapping levels
    enum Level {
        ENDPOINT, IO, TYPE
    }

    /**
     * Checks if this endpoint mapping applies to the given level and schema.
     *
     * @param level the level to match
     * @param schema the schema to match
     * @return true, if the mapping applies, false if not
     */
    boolean matches (Level level, MappingSchema schema)

    /**
     * Checks if this endpoint mapping applied to the given schema type.
     *
     * @param level the level to match
     * @param schemaType the schema type to match
     * @return true, if the mapping applies, false if not
     */
    boolean matches (Level level, MappingSchemaType schemaType)

    /**
     * Returns the inner mappings.
     * In case of an ENDPOINT mapping the IO or TYPE mappings.
     * In case of an IO mappings its type mappings.
     * In case of a TYPE mapping itself.
     *
     * @return the inner type mappings.
     */
    List<Mapping> getChildMappings ()

}
