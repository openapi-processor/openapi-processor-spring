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

package com.github.hauner.openapi.spring.generatr.mapping

/**
 * Used with {@link com.github.hauner.openapi.spring.generatr.ApiOptions} to globally override the
 * default mapping of the OpenAPI {@code array} from a java array to another collection type. All
 * usages of {@code array} in the api description will be replaced with the given java class in the
 * generated code.
 *
 * Only one mapping of this kind is allowed.
 *
 * @author Martin Hauner
 */
class ArrayTypeMapping {

    /**
     * The fully qualified java type name of the collection type that should be used for the OpenAPI
     * {@code array} type.
     *
     * Note that {@link #targetTypeName} is limited to the following values:
     * - 'java.util.Collection'
     */
    String targetTypeName
}
