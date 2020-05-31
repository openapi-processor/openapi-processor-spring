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

package com.github.hauner.openapi.core.parser.openapi4j

import com.github.hauner.openapi.core.parser.Schema as ParserSchema
import org.openapi4j.parser.model.v3.Schema as O4jSchema

/**
 * openapi4j Schema abstraction.
 *
 * @author Martin Hauner
 */
class Schema implements ParserSchema {

    O4jSchema schema

    Schema (O4jSchema schema) {
        this.schema = schema
    }

    @Override
    String getType () {
        if (itemsOf () != null) {
            return 'composed'
        }

        schema.type
    }

    @Override
    String getFormat () {
        schema.format
    }

    @Override
    String getRef () {
        schema.ref
    }

    @Override
    List<?> getEnum () {
        schema.enums
    }

    @Override
    ParserSchema getItem () {
        new Schema(schema.itemsSchema)
    }

    @Override
    Map<String, ParserSchema> getProperties () {
        def props = new LinkedHashMap<String, ParserSchema> ()
        schema.properties.each { Map.Entry<String, O4jSchema> entry ->
            props.put (entry.key, new Schema (entry.value))
        }
        props
    }

    @Override
    List<ParserSchema> getItems () {
        List<ParserSchema> result = []

        schema.allOfSchemas.each {
            result.add (new Schema(it))
        }

        schema.anyOfSchemas.each {
            result.add (new Schema(it))
        }

        schema.oneOfSchemas.each {
            result.add (new Schema(it))
        }

        result
    }

    @Override
    String itemsOf () {
        if (schema.allOfSchemas != null) {
            return 'allOf'
        }

        if (schema.anyOfSchemas != null) {
            return 'anyOf'
        }

        if (schema.oneOfSchemas != null) {
            return 'oneOf'
        }

        null
    }

    @Override
    def getDefault () {
        schema.default
    }

    @Override
    Boolean getNullable () {
        schema.nullable
    }

    @Override
    Integer getMinLength () {
        schema.minLength
    }

    @Override
    Integer getMaxLength () {
        schema.maxLength
    }

    @Override
    Integer getMinItems () {
        schema.minItems
    }

    @Override
    Integer getMaxItems () {
        schema.maxItems
    }

    @Override
    BigDecimal getMaximum () {
        schema.maximum
    }

    @Override
    Boolean isExclusiveMaximum () {
        schema.exclusiveMaximum
    }

    @Override
    BigDecimal getMinimum () {
        schema.minimum
    }

    @Override
    Boolean isExclusiveMinimum () {
        schema.exclusiveMinimum
    }

}
