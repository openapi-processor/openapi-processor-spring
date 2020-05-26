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

package com.github.hauner.openapi.spring.converter.mapping

/**
 * Used with {@link EndpointTypeMapping} to configure an additional endpoint parameter that is not
 * defined in the api description.
 *
 * @author Martin Hauner
 */
class AddParameterTypeMapping(

    /**
     * The parameter name of this mapping.
     */
    val parameterName: String,

    /**
     * additional parameter type mapping.
     */
    val mapping: TypeMapping

): Mapping {

    override fun matches(visitor: MappingVisitor): Boolean {
        return visitor.match(this)
    }

    override fun getChildMappings(): List<Mapping> {
        return listOf(mapping)
    }

}
