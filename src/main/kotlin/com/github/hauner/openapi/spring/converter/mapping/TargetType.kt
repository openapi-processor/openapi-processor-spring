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
 * Mapping target result created from {@link TypeMapping}.
 */
class TargetType(

    /**
     * type name
     */
    val typeName: String,

    /**
     * generic parameters of typeName
     */
    val genericNames: List<String>

) {

    /**
     * Returns the class name without the package name.
     *
     * @return the class name
     */
    fun getName(): String {
        return typeName.substring(typeName.lastIndexOf('.') + 1)
    }

    /**
     * Returns the package name.
     *
     * @return the package name
     */
    fun getPkg(): String {
        return typeName.substring(0, typeName.lastIndexOf('.'))
    }

}
