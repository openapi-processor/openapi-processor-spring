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

package com.github.hauner.openapi.spring.generatr

import com.github.hauner.openapi.spring.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.spring.converter.mapping.TypeMapping
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class TypeMappingReaderSpec extends Specification {

    @Rule
    TemporaryFolder folder

    void "ignores empty type mapping" () {
        def yaml = ""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings == null
    }

    void "reads mapping from file" () {
        def yaml = """\
openapi-generatr-spring: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        def yamlFile = folder.newFile ("openapi-generatr-spring.yaml")
        yamlFile.text = yaml

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yamlFile.absolutePath)

        then:
        mappings.size () == 1
    }

    void "reads global type mapping" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size () == 1
        def type = mappings.first () as TypeMapping
        type.sourceTypeName == 'array'
        type.sourceTypeFormat == null
        type.targetTypeName == 'java.util.Collection'
        type.genericTypeNames == []
    }

    void "reads global type mapping with generic types" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  types:
    # inline format
    - from: Foo
      to: mapping.Bar<java.lang.String, java.lang.Boolean>

    # long format
    - from: Foo2
      to: mapping.Bar2
      generics:
        - java.lang.String2
        - java.lang.Boolean2
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size () == 2

        def shortFormat = mappings.first () as TypeMapping
        shortFormat.sourceTypeName == 'Foo'
        shortFormat.sourceTypeFormat == null
        shortFormat.targetTypeName == 'mapping.Bar'
        shortFormat.genericTypeNames == ['java.lang.String', 'java.lang.Boolean']

        def longFormat = mappings[1] as TypeMapping
        longFormat.sourceTypeName == 'Foo2'
        longFormat.sourceTypeFormat == null
        longFormat.targetTypeName == 'mapping.Bar2'
        longFormat.genericTypeNames == ['java.lang.String2', 'java.lang.Boolean2']
    }

    void "reads global type mapping with format" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  types:
    - from: string:date-time
      to: java.time.ZonedDateTime
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size () == 1
        def type = mappings.first () as TypeMapping
        type.sourceTypeName == 'string'
        type.sourceTypeFormat == 'date-time'
        type.targetTypeName == 'java.time.ZonedDateTime'
        type.genericTypeNames == []
    }

    void "reads global response type mapping" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  responses:
    - content: application/vnd.array
      to: java.util.List
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size() == 1

        def response = mappings.first () as ResponseTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypeNames == []
    }

    void "reads endpoint response type mapping " () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  paths:
    /foo:
      responses:
        - content: application/vnd.array
          to: java.util.List
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def response = endpoint.typeMappings.first () as ResponseTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypeNames == []
    }

    void "reads global parameter type mapping" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  parameters:
    - name: foo
      to: mapping.Foo
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size() == 1

        def parameter = mappings.first () as ParameterTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'mapping.Foo'
        parameter.mapping.genericTypeNames == []
    }

    void "reads endpoint parameter type mapping" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  paths:
    /foo:
      parameters:
        - name: foo
          to: mapping.Foo
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1
        def parameter = endpoint.typeMappings.first () as ParameterTypeMapping
        parameter.parameterName == 'foo'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'mapping.Foo'
        parameter.mapping.genericTypeNames == []
    }

    void "reads endpoint type mapping" () {
        String yaml = """\
openapi-generatr-spring: v1.0
    
map:
  paths:
    /foo:
      types:
        - from: array
          to: java.util.Collection
"""

        when:
        def reader = new TypeMappingReader()
        def mappings = reader.read (yaml)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 1

        def type = endpoint.typeMappings.first () as TypeMapping
        type.sourceTypeName == 'array'
        type.sourceTypeFormat == null
        type.targetTypeName == 'java.util.Collection'
        type.genericTypeNames == []
    }
}
