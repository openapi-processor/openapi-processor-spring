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
 * OpenAPI schema mapped to a java type.
 *
 * @author Martin Hauner
 */
class MappedDataType implements DataType {

    private String type
    String pkg = 'unknown'
    List<String> genericTypes = []

    @Override
    String getName () {
        if (genericTypes.empty) {
            "${type}"
        } else {
            "${type}<${getGenericTypeNames().join (', ')}>"
        }
    }

    @Override
    String getPackageName () {
        pkg
    }

    @Override
    String getImport () {
        [packageName, type].join ('.')
    }

    @Override
    Set<String> getImports () {
         [getImport ()] + genericTypes
    }

    private List<String> getGenericTypeNames () {
        genericTypes.collect {
            getClassName (it)
        }
    }

    private String getClassName (String ref) {
        ref.substring (ref.lastIndexOf ('.') + 1)
    }

}
