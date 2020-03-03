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

package com.github.hauner.openapi.spring.converter

/**
 * thrown when the DataTypeConverter hits an unknown data type.
 *
 * @author Martin Hauner
 */
class UnknownDataTypeException extends RuntimeException {

    String name
    String type
    String format

    UnknownDataTypeException(String name, String type, String format) {
        super()
        this.name = name
        this.type = type
        this.format = format
    }

    @Override
    String getMessage () {
        "unknown schema: ${name} of type $type${format ? "/" + format: ''}"
    }

}
