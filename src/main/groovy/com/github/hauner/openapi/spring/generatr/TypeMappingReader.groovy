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

package com.github.hauner.openapi.spring.generatr

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.github.hauner.openapi.spring.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import com.github.hauner.openapi.spring.generatr.mapping.AdditionalParameter
import com.github.hauner.openapi.spring.generatr.mapping.Mapping as YamlMapping
import com.github.hauner.openapi.spring.generatr.mapping.Parameter
import com.github.hauner.openapi.spring.generatr.mapping.Parameter as YamlParameter
import com.github.hauner.openapi.spring.generatr.mapping.ParameterDeserializer
import com.github.hauner.openapi.spring.generatr.mapping.Path as YamlPath
import com.github.hauner.openapi.spring.generatr.mapping.RequestParameter
import com.github.hauner.openapi.spring.generatr.mapping.Response as YamlResponse
import com.github.hauner.openapi.spring.generatr.mapping.Type as YamlType

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *  Reader for mapping yaml.
 *
 *  @author Martin Hauner
 */
@Deprecated
class TypeMappingReader {
    private Pattern GENERIC_INLINE = ~/(.+?)<(.+?)>/

    List<Mapping> read (String typeMappings) {
        if (typeMappings == null || typeMappings.empty) {
            return []
        }

        String mapping = typeMappings
        if (isFileName (typeMappings)) {
            mapping = new File(typeMappings).text
        }

        def mapper = createYamlParser()
        def props = mapper.readValue (mapping, YamlMapping)

        convert (props)
    }

    private List<Mapping> convert (YamlMapping source) {
        def result = new ArrayList<Mapping>()

        source.map.types.each {
            result.add (convert (it))
        }

        source.map.parameters.each {
            result.add (convert (it))
        }

        source.map.responses.each {
            result.add (convert (it))
        }

        source.map.paths.each {
            result.add(convert (it.key, it.value))
        }

        result
    }

    private EndpointTypeMapping convert (String path, YamlPath source) {
        def result = new ArrayList<Mapping>()

        source.types.each {
            result.add (convert (it))
        }

        source.parameters.each {
            result.add (convert (it))
        }

        source.responses.each {
            result.add (convert (it))
        }

        new EndpointTypeMapping(path: path, typeMappings: result)
    }

    private Mapping convert (YamlParameter source) {
        if (source instanceof RequestParameter) {
            def name = source.name
            def mapping = convert (new YamlType(
                from: null,
                to: source.to,
                generics: source.generics
            ))
            new ParameterTypeMapping (parameterName: name, mapping: mapping)

        } else if (source instanceof  AdditionalParameter) {
            def name = source.add
            def mapping = convert (new YamlType(
                from: null,
                to: source.to,
                generics: source.generics
            ))
            new AddParameterTypeMapping (parameterName: name, mapping: mapping)

        } else {
            throw new Exception("unknown parameter mapping $source")
        }
    }

    private ResponseTypeMapping convert (YamlResponse source) {
        def content = source.content
        def mapping = convert (new YamlType(
            from: null,
            to: source.to,
            generics: source.generics
        ))
        new ResponseTypeMapping(contentType: content, mapping: mapping)
    }

    private TypeMapping convert (YamlType type) {
        Matcher matcher = type.to =~ GENERIC_INLINE

        def (from, format) = type.from ? (type.from as String).tokenize (':') : [null,  null]
        String to = type.to
        List<String> generics = []

        // has inline generics
        if (matcher.find ()) {
            to = matcher.group (1)
            generics = matcher
                .group (2)
                .split (',')
                .collect { it.trim () }

        // has explicit generic list
        } else if (type.generics) {
            generics = type.generics
        }

        new TypeMapping (
            sourceTypeName: from, sourceTypeFormat: format,
            targetTypeName: to, genericTypeNames: generics)
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
