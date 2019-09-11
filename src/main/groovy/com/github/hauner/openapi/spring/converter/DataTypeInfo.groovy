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

import io.swagger.v3.oas.models.media.Schema

/**
 * Helper for DataTypeConverter. Holds an OpenAPI schema and context information, i.e. name and if
 * this is an inline type with a generated name.
 *
 * @author Martin Hauner
 */
class DataTypeInfo {
    Schema schema
    String name
    boolean inline = false

    DataTypeInfo (Schema schema, String name) {
        this.schema = schema
        this.name = name
    }

    DataTypeInfo (Schema schema, String name, boolean inline) {
        this.schema = schema
        this.name = name
        this.inline = inline
    }

    boolean isArray () {
        schema.type == 'array'
    }

    boolean isObject () {
        schema.type == 'object'
    }

    boolean isRefObject () {
        schema.$ref != null
    }

}
