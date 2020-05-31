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

package com.github.hauner.openapi.core.parser.swagger

import com.github.hauner.openapi.core.parser.Schema as ParserSchema
import io.swagger.v3.oas.models.media.Schema as SwaggerSchema
import io.swagger.v3.oas.models.media.ArraySchema as SwaggerArraySchema
import io.swagger.v3.oas.models.media.ComposedSchema as SwaggerComposedSchema

/**
 * Swagger Schema abstraction.
 *
 * @author Martin Hauner
 */
class Schema implements ParserSchema {

    SwaggerSchema schema

    Schema (SwaggerSchema schema) {
        this.schema = schema
    }

    @Override
    String getType () {
        if (schema instanceof SwaggerComposedSchema) {
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
        schema.$ref
    }

    @Override
    List<?> getEnum () {
        schema.enum
    }

    @Override
    ParserSchema getItem () {
        new Schema((schema as SwaggerArraySchema).items)
    }

    @Override
    Map<String, ParserSchema> getProperties () {
        def props = new LinkedHashMap<String, ParserSchema>()
        schema.properties.each { Map.Entry<String, SwaggerSchema> entry ->
            props.put (entry.key, new Schema(entry.value))
        }
        props
    }

    List<ParserSchema> getItems () {
        List<ParserSchema> result = []

        def composed = schema as SwaggerComposedSchema

        composed.allOf.each {
            result.add (new Schema(it))
        }

        composed.anyOf.each {
            result.add (new Schema(it))
        }

        composed.oneOf.each {
            result.add (new Schema(it))
        }

        result
    }

    @Override
    String itemsOf () {
        def composed = schema as SwaggerComposedSchema

        if (composed.allOf != null) {
            return 'allOf'
        }

        if (composed.anyOf != null) {
            return 'anyOf'
        }

        if (composed.oneOf != null) {
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
