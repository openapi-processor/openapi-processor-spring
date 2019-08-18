/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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
 */
class CompositeDataType implements DataType {

    String type
    String pkg = 'unknown'

    Map<String, DataType> properties = new LinkedHashMap<>()

    @Override
    List<String> getImports () {
        List<String> imports = []
        properties.values ().each {
            imports.add ("${it.packageName}.${it.type}")
        }
        imports
    }

    void addProperty(String name, DataType type) {
        properties.put (name, type)
    }

    String getPackageName() {
        pkg
    }

    String getDataTypeName() {
        type
    }
}
