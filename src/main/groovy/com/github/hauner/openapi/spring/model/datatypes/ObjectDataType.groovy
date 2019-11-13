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
 * OpenAPI named #/component/schemas type or an inline type.
 *
 * @author Martin Hauner
 */
class ObjectDataType implements DataType {

    String type
    String pkg = 'unknown'

    // must preserve the insertion order
    Map<String, DataType> properties = new LinkedHashMap<> ()

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
        properties.values ().each {
            imports.addAll (it.imports)
        }
        imports
    }

    void addObjectProperty (String name, DataType type) {
        properties.put (name, type)
    }

    DataType getObjectProperty (String name) {
        properties.get (name)
    }

}
