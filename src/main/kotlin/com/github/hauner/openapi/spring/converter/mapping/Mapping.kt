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

    /**
     * check if this mapping applies to the given schema by delegating to the visitor.
     *
     * @param visitor provides the matching logic
     * @return true, id mapping applies, false otherwise
     */
    fun matches (visitor: MappingVisitor): Boolean

    /**
     * Returns the inner mappings.
     * In case of an ENDPOINT mapping the IO or TYPE mappings.
     * In case of an IO mappings its type mappings.
     * In case of a TYPE or RESULT mapping itself.
     *
     * @return the inner type mappings.
     */
    fun getChildMappings (): List<Mapping>

}
