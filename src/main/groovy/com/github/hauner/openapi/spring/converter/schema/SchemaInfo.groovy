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

package com.github.hauner.openapi.spring.converter.schema

import com.github.hauner.openapi.spring.converter.mapping.MappingSchema
import groovy.transform.stc.ClosureParams
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.Schema

/**
 * Helper for {@link com.github.hauner.openapi.spring.converter.DataTypeConverter}. Holds an OpenAPI
 * schema with context information, i.e. name and if this is an inline type with a generated name.
 *
 * @author Martin Hauner
 */
class SchemaInfo implements MappingSchema {

    /**
     * Endpoint path.
     */
    String path

    /**
     * name of the type/schema or parameter name.
     */
    String name

    /**
     * response content type.
     */
    String contentType

    /**
     * the OpenAPI schema
     */
    Schema schema

    /**
     * resolver of $ref'erences
     */
    RefResolver resolver

    void eachProperty (@ClosureParams({"String,DataTypeInfo"}) Closure closure) {
        schema.properties.each { Map.Entry<String, Schema> entry ->
            closure (entry.key, buildForNestedType (entry.key, entry.value))
        }
    }

    /**
     * Factory method to create a {@code DataTypeInfo} with the $ref name (without "path").
     *
     * @return a new DataTypeInfo
     */
    SchemaInfo buildForRef () {
        new SchemaInfo (
            path: path,
            name: getRefName (schema),
            schema: resolver.resolve (schema.$ref),
            resolver: resolver)
    }

    /**
     * Factory method to create an inline {@code DataTypeInfo} with (property) name and (property)
     * schema.
     *
     * @param nestedName the property name
     * @param nestedSchema the property schema
     * @return a new DataTypeInfo
     */
    SchemaInfo buildForNestedType (String nestedName, Schema nestedSchema) {
        new SchemaInfo (
            path: path,
            name: getNestedTypeName (nestedName),
            schema: nestedSchema,
            resolver: resolver)
    }

    /**
     * Factory method to create an {@code DataTypeInfo} of the item type of an array schema.
     *
     * @return s new DataTypeInfo
     */
    SchemaInfo buildForItem () {
        new SchemaInfo (
            path: path,
            name: (schema as ArraySchema).items.type,
            schema: (schema as ArraySchema).items,
           resolver: resolver)
    }

    /**
     * get type from OpenAPI schema.
     *
     * @return schema type
     */
    String getType () {
        schema.type
    }

    /**
     * get type format from OpenAPI schema.
     *
     * @return schema type format
     */
    String getFormat () {
        schema.format
    }

    /**
     * get $ref from OpenAPI schema.
     *
     * @return schema $ref
     */
    String getRef () {
        schema.$ref
    }

    /**
     * get default value.
     *
     * @return default value or null
     */
    def getDefaultValue() {
        schema.default
    }

    /**
     * get the custom Java type (fully qualified) defined via the {@code x-java-type} OpenAPI
     * extension. If no {@code x-java-type} is set the result is {@code null}.
     *
     * @return fully qualified name of the Java type or null if not set
     */
    @Deprecated
    String getXJavaType () {
        if (!hasExtensions ()) {
            return null
        }

        schema.extensions.get ('x-java-type')
    }

    boolean isArray () {
        schema.type == 'array'
    }

    boolean isObject () {
        schema.type == 'object'
    }

    boolean isRefObject () {
        schema.$ref != null
    }

    boolean isEnum () {
        schema.enum != null
    }

    List<?> getEnumValues () {
        schema.enum
    }

    private boolean hasExtensions () {
        schema.extensions != null
    }

    private String getRefName (Schema schema) {
        schema.$ref.substring (schema.$ref.lastIndexOf ('/') + 1)
    }

    private String getNestedTypeName (String propName) {
        name + propName.capitalize ()
    }

}
