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

import com.github.hauner.openapi.spring.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import com.github.hauner.openapi.spring.converter.mapping.Mapping
import org.yaml.snakeyaml.Yaml

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *  Reader for mapping yaml.
 *
 *  @author Martin Hauner
 */
class TypeMappingReader {
    private Pattern GENERIC_INLINE = ~/(.+?)<(.+?)>/

    List<Mapping> read (String typeMappings) {
        if (typeMappings == null) {
            return []
        }

        String mapping = typeMappings
        if (isFileName (typeMappings)) {
            mapping = new File(typeMappings).text
        }

        Yaml yaml = new Yaml()
        Map props = yaml.load (mapping)

        if (props == null) {
            return null
        }

        parse (props)
    }

    private List<Mapping> parse (Map<String, ?> props) {
        //def version = props.get ('openapi-generatr-spring')

        def root = props.get ('map') as Map<String, ?>

        def mappings = readTypeMappings (root)

        def paths = root.get ('paths') as Map<String, ?>
        paths.each {
            def epm = new EndpointTypeMapping(path: it.key)
            def types = readTypeMappings (it.value as Map<String, ?>)
            epm.typeMappings = types
            mappings.add (epm)
        }

        mappings
    }

    private List<Mapping> readTypeMappings (Map<String, ?> root) {
        def mappings = []

        def types = root.get ('types') as List<Map<String, ?> >
        types.each { Map<String, ?> it ->
            mappings.add (readTypMapping (it))
        }

        def responses = root.get ('responses') as List<Map<String, ?> >
        responses.each {
            mappings.add (readResponseTypeMapping (it))
        }

        def parameters = root.get ('parameters') as List<Map<String, ?> >
        parameters.each {
            mappings.add (readParameterTypeMapping (it))
        }

        return mappings
    }

    private Mapping readParameterTypeMapping (Map<String, ?> source) {
        if (isParameterMappings (source)) {
            def name = source.name
            def mapping = readTypMapping (source)
            new ParameterTypeMapping (parameterName: name, mapping: mapping)

        } else if (isParameterAddition (source)) {
            def name = source.add
            def mapping = readTypMapping (source, 'as')
            new AddParameterTypeMapping (parameterName: name, mapping: mapping)

        } else {
            throw new Exception("unknown parameter mapping $source")
        }
    }

    private boolean isParameterAddition (Map<String, ?> source) {
        source.containsKey ('add') && source.containsKey ('as')
    }

    private boolean isParameterMappings (Map<String, ?> source) {
        source.containsKey ('name') && source.containsKey ('to')
    }

    private ResponseTypeMapping readResponseTypeMapping (Map<String, ?> source) {
        def content = source.content
        def mapping = readTypMapping (source)
        new ResponseTypeMapping(contentType: content, mapping: mapping)
    }

    private TypeMapping readTypMapping (Map<String, ?> source, String target = 'to') {
        Matcher matcher = source.to =~ GENERIC_INLINE

        def (from, format) = source.from ? (source.from as String).tokenize (':') : [null,  null]
        String to = source[target]
        List<String> generics = []

        // has inline generics
        if (matcher.find ()) {
            to = matcher.group (1)
            generics = matcher
                .group (2)
                .split (',')
                .collect { it.trim () }

        // has explicit generic list
        } else if (source.containsKey ('generics')) {
            generics = source.generics as List
        }

        new TypeMapping (
            sourceTypeName: from, sourceTypeFormat: format,
            targetTypeName: to, genericTypeNames: generics)
    }

    private boolean isFileName (String name) {
        name.endsWith ('.yaml') || name.endsWith ('.yml')
    }

}
