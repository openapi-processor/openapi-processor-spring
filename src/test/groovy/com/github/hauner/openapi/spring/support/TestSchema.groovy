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

package com.github.hauner.openapi.spring.support

import com.github.hauner.openapi.spring.parser.Schema

/**
 * simple Schema implementation for testing
 */
class TestSchema implements Schema {
    String type
    String format

    String ref

    def defaultValue
    Boolean nullable
    Integer minLength
    Integer maxLength
    Integer minItems
    Integer maxItems
    BigDecimal maximum
    Boolean exclusiveMaximum
    BigDecimal minimum
    Boolean exclusiveMinimum

    Schema item
    Map<String, Schema> properties
    List<?> enumValues

    def getDefault() {
        defaultValue
    }

    @Override
    Boolean isExclusiveMaximum () {
        exclusiveMaximum
    }

    @Override
    Boolean isExclusiveMinimum () {
        exclusiveMinimum
    }


    @Override
    List<?> getEnum () {
        return enumValues
    }

}
