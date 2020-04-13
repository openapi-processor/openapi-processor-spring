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

/**
 * OpenAPI type 'array' maps to java [].
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
 */
class ArrayDataType implements DataType {

    private DataType item
    private DataTypeConstraints constraints

    @Override
    String getName () {
        "${item.name}[]"
    }

    @Override
    String getPackageName () {
        item.packageName
    }

    @Override
    Set<String> getImports () {
        item.imports
    }

    @Override
    Set<String> getReferencedImports () {
        item.referencedImports
    }

    @Override
    DataTypeConstraints getConstraints () {
        constraints
    }
    
}
