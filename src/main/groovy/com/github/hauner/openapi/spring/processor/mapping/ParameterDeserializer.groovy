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
package com.github.hauner.openapi.spring.processor.mapping

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

/**
 * deserializer for parameter sub types
 *
 *  @author Martin Hauner
 */
class ParameterDeserializer extends StdDeserializer<Parameter> {

    ParameterDeserializer () {
        super(Parameter)
    }

    @Override
    Parameter deserialize (JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
        java.util.Map<String, ?> props = ctx.readValue (jp, java.util.Map)

        if (isRequestParameterMapping (props)) {
            return new RequestParameter(
                name: props.get ('name'),
                to: props.get ('to'),
                generics: props.get ('generics') as List<String>
            )
        }

        if (isAdditionalParameterMapping (props)) {
            return new AdditionalParameter(
                add: props.get ('add'),
                to: props.get ('as'),
                generics: props.get ('generics')  as List<String>
            )
        }

        throw new IOException("unknown parameter type at: " + jp.tokenLocation.toString ())
    }

    private boolean isRequestParameterMapping (java.util.Map<String, ?> source) {
        source.containsKey ('name') && source.containsKey ('to')
    }

    private boolean isAdditionalParameterMapping (java.util.Map<String, ?> source) {
        source.containsKey ('add') && source.containsKey ('as')
    }
    
}
