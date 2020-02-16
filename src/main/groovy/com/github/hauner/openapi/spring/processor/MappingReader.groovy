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

package com.github.hauner.openapi.spring.processor

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.hauner.openapi.spring.processor.mapping.Mapping
import com.github.hauner.openapi.spring.processor.mapping.Parameter
import com.github.hauner.openapi.spring.processor.mapping.ParameterDeserializer

import java.util.regex.Pattern

/**
 *  Reader for mapping yaml.
 *
 *  @author Martin Hauner
 */
class MappingReader {
    private Pattern GENERIC_INLINE = ~/(.+?)<(.+?)>/

    Mapping read (String typeMappings) {
        if (typeMappings == null || typeMappings.empty) {
            return null
        }

        String mapping = typeMappings
        if (isFileName (typeMappings)) {
            mapping = new File (typeMappings).text
        }

        def mapper = createYamlParser ()

        mapper.readValue (mapping, Mapping)
    }

    private ObjectMapper createYamlParser () {
        SimpleModule module = new SimpleModule ()
        module.addDeserializer (Parameter, new ParameterDeserializer ())

        new ObjectMapper (new YAMLFactory ())
            .configure (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setPropertyNamingStrategy (PropertyNamingStrategy.KEBAB_CASE)
            .registerModule (module)
    }

    private boolean isFileName (String name) {
        name.endsWith ('.yaml') || name.endsWith ('.yml')
    }

}
