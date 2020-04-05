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

package com.github.hauner.openapi.spring.model.datatypes

/**
 * Single data type wrapper. Assumes a single generic parameter.
 *
 * @author Martin Hauner
 */
class SingleDataType implements DataType {

    String type
    String pkg = 'unknown'
    private DataType dataType

    @Override
    String getName () {
        "$type<${dataType.name}>"
    }

    @Override
    String getPackageName () {
        pkg
    }

    @Override
    Set<String> getImports () {
        [[packageName, type].join ('.')] + dataType.imports
    }

    @Override
    Set<String> getReferencedImports () {
        []
    }

    @Override
    DataTypeConstraints getConstraints () {
        null
    }

}
