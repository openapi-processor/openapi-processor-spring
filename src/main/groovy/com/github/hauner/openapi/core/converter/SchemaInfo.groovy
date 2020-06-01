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

package com.github.hauner.openapi.core.converter

import com.github.hauner.openapi.core.converter.mapping.MappingSchema
import com.github.hauner.openapi.core.parser.RefResolver as ParserRefResolver
import com.github.hauner.openapi.core.parser.Schema

/**
 * Helper for {@link com.github.hauner.openapi.spring.converter.DataTypeConverter}. Holds an OpenAPI
 * schema with context information, i.e. name and if this is an inline type with a generated name.
 *
 * @author Martin Hauner
 * @author Bastian Wilhelm
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
    ParserRefResolver resolver

    void eachProperty (/* Closure Params: String, SchemaInfo */ Closure closure) {
        schema.properties.each { Map.Entry<String, Schema> entry ->
            closure (entry.key, buildForNestedType (entry.key, entry.value))
        }
    }

    void eachItemOf (/* Closure Params: SchemaInfo */ Closure closure) {
        schema.items.eachWithIndex { it, idx ->
            closure (new SchemaInfo (
                path: path,
                name: "$name-of-$idx",
                schema: it,
                resolver: resolver
            ))
        }
    }

    String itemOf () {
        schema.itemsOf ()
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
            schema: resolver.resolve (schema),
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
            name: schema.item.type,
            schema: schema.item,
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
        schema.ref
    }

    /**
     * get default value.
     *
     * @return default value or null
     */
    def getDefaultValue () {
        schema.default
    }

    /**
     * get nullable value.
     *
     * @return nullable value or null
     */
    def getNullable () {
        schema.nullable
    }

    /**
     * get minLength value.
     *
     * @return minLength value or null
     */
    def getMinLength () {
        schema.minLength
    }

    /**
     * get maxLength value.
     *
     * @return maxLength value or null
     */
    def getMaxLength () {
        schema.maxLength
    }

    /**
     * get minItems value.
     *
     * @return minItems value or null
     */
    def getMinItems () {
        schema.minItems
    }

    /**
     * get maxItems value.
     *
     * @return maxItems value or null
     */
    def getMaxItems () {
        schema.maxItems
    }

    /**
     * get maximum value.
     *
     * @return maximum value or null
     */
    def getMaximum () {
        schema.maximum
    }

    /**
     * get exclusiveMaximum value.
     *
     * @return exclusiveMaximum value or null
     */
    def getExclusiveMaximum () {
        schema.exclusiveMaximum
    }

    /**
     * get minimum value.
     *
     * @return minimum value or null
     */
    def getMinimum () {
        schema.minimum
    }

    /**
     * get exclusiveMinimum value.
     *
     * @return exclusiveMinimum value or null
     */
    def getExclusiveMinimum () {
        schema.exclusiveMinimum
    }

    boolean isPrimitive () {
        schema.type in ['boolean', 'int', 'number', 'string']
    }

    boolean isArray () {
        schema?.type == 'array'
    }

    boolean isObject () {
        schema.type == 'object'
    }

    boolean isComposedObject () {
        schema.type == 'composed'
    }

    boolean isRefObject () {
        schema.ref != null
    }

    boolean isEmpty () {
        !schema
    }

    boolean isEnum () {
        schema.enum != null
    }

    List<?> getEnumValues () {
        schema.enum
    }

    private String getRefName (Schema schema) {
        schema.ref.substring (schema.ref.lastIndexOf ('/') + 1)
    }

    private String getNestedTypeName (String propName) {
        name + propName.capitalize ()
    }

}
