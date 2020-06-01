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

package com.github.hauner.openapi.core.converter.mapping

/**
 * Used with {@link com.github.hauner.openapi.spring.converter.ApiOptions#typeMappings} to map an
 * OpenAPI schema to a java type.
 *
 * To override the type mapping of the OpenAPI {@code array} from a simple java array to another
 * collection type the @(link #soucrceTypeName) should be set to {@code array}.
 *
 * @author Martin Hauner
 */
class TypeMapping (

    /**
     * The OpenAPI schema type that should be mapped to the {@link #targetTypeName} java type.
     */
    val sourceTypeName: String?,

    /**
     * The OpenAPI format of {@link #sourceTypeName} that should be mapped to the
     * {@link #targetTypeName} java type.
     */
    val sourceTypeFormat: String?,

    /**
     * The fully qualified java type name that will replace {@link #sourceTypeName}.
     */
    val targetTypeName: String,

    /**
     * The fully qualified java type names of all generic parameters to {@link #targetTypeName}.
     */
    val genericTypeNames: List<String> = emptyList()

): Mapping, TargetTypeMapping {

    constructor(sourceTypeName: String?, targetTypeName: String):
            this (sourceTypeName, null, targetTypeName, emptyList())

    constructor(sourceTypeName: String?, sourceTypeFormat: String?, targetTypeName: String):
            this (sourceTypeName, sourceTypeFormat, targetTypeName, emptyList())

    constructor(sourceTypeName: String?, targetTypeName: String, genericTypeNames: List<String>):
            this (sourceTypeName, null, targetTypeName, genericTypeNames)

    /**
     * Returns the full source type as {@link #sourceTypeName} and {@link #sourceTypeFormat} joined
     * by a ':' separator.
     *
     * @return the full source type
     */
    /*
    @Deprecated("do not use in new code", ReplaceWith("no replacement"))
    fun getFullSourceType(): String {
        return sourceTypeName + (sourceTypeFormat ? ":$sourceTypeFormat" : "")
    }
    */

    /**
     * Returns the target type of this type mapping.
     *
     * @return the target type
     */
    override fun getTargetType (): TargetType {
        return TargetType(targetTypeName, genericTypeNames)
    }

    override fun matches(visitor: MappingVisitor): Boolean {
        return visitor.match (this)
    }

    override fun getChildMappings(): List<Mapping> {
        return listOf(this)
    }

}
