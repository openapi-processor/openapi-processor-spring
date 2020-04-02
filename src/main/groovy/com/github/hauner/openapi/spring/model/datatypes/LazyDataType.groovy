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

package com.github.hauner.openapi.spring.model.datatypes

import com.github.hauner.openapi.spring.converter.SchemaInfo
import com.github.hauner.openapi.spring.model.DataTypes

/**
 * OpenAPI $ref type that is lazily evaluated. It is used to break loops in the schema definitions.
 *
 * @autor Martin Hauner
 */
class LazyDataType implements DataType {

    private DataTypes dataTypes
    private SchemaInfo info

    @Override
    String getName () {
        dataType.name
    }

    @Override
    String getPackageName () {
        dataType.packageName
    }

    @Override
    Set<String> getImports () {
        dataType.imports
    }

    @Override
    Set<String> getReferencedImports () {
        dataType.referencedImports
    }

    private DataType getDataType () {
        dataTypes.find (info.name)
    }

}
