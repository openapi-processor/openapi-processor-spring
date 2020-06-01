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

package com.github.hauner.openapi.core.model.datatypes

/**
 * OpenAPI composed schema type.
 *
 * @author Martin Hauner
 */
class ComposedObjectDataType implements DataType {

    String type
    String pkg = 'unknown'
    String of

    List<DataType> items = []
    private DataTypeConstraints constraints

    @Override
    String getName () {
        type
    }

    @Override
    String getPackageName () {
        pkg
    }

    @Override
    Set<String> getImports () {
        [[packageName, name].join ('.')]
    }

    @Override
    Set<String> getReferencedImports () {
        List<String> imports = []
        items.each {
            imports.addAll (it.imports)
        }
        imports
    }

    void addItems (DataType type) {
        items.add (type)
    }

    @Override
    DataTypeConstraints getConstraints () {
        constraints
    }

    boolean isMultiOf () {
        of != 'allOf'
    }


}
