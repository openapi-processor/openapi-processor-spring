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

package com.github.hauner.openapi.spring.converter.mapping

import com.github.hauner.openapi.spring.converter.schema.SchemaInfo

/**
 * Type mapping levels
 */
enum MappingLevel {
    ENDPOINT, IO, TYPE
}

/**
 * Common interface for type mappings.
 */
interface TypeMappingX {
    boolean matches (SchemaInfo info)
    boolean isLevel (MappingLevel level)
    List<TypeMappingX> getChildMappings ()
}
