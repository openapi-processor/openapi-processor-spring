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

package com.github.hauner.openapi.spring.processor

import com.github.hauner.openapi.core.converter.mapping.AddParameterTypeMapping
import com.github.hauner.openapi.core.converter.mapping.EndpointTypeMapping
import com.github.hauner.openapi.core.converter.mapping.ParameterTypeMapping
import com.github.hauner.openapi.core.converter.mapping.ResponseTypeMapping
import com.github.hauner.openapi.core.converter.mapping.ResultTypeMapping
import com.github.hauner.openapi.core.converter.mapping.TypeMapping
import spock.lang.Specification
import spock.lang.Subject

class MappingConverterSpec extends Specification {

    def reader = new MappingReader()

    @Subject
    def converter = new MappingConverter()

    void "reads global type mapping" () {
        String yaml = """\
openapi-processor-spring: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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
openapi-processor-spring: v1.0
    
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
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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
openapi-processor-spring: v1.0
    
map:
  types:
    - from: string:date-time
      to: java.time.ZonedDateTime
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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
openapi-processor-spring: v1.0
    
map:
  responses:
    - content: application/vnd.array
      to: java.util.List
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def response = mappings.first () as ResponseTypeMapping
        response.contentType == 'application/vnd.array'
        response.mapping.sourceTypeName == null
        response.mapping.sourceTypeFormat == null
        response.mapping.targetTypeName == 'java.util.List'
        response.mapping.genericTypeNames == []
    }

    void "reads endpoint response type mapping" () {
        String yaml = """\
openapi-processor-spring: v1.0
    
map:
  paths:
    /foo:
      responses:
        - content: application/vnd.array
          to: java.util.List
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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
openapi-processor-spring: v1.0
    
map:
  parameters:
    - name: foo
      to: mapping.Foo
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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
openapi-processor-spring: v1.0
    
map:
  paths:
    /foo:
      parameters:
        - name: foo
          to: mapping.Foo
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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
openapi-processor-spring: v1.0
    
map:
  paths:
    /foo:
      types:
        - from: array
          to: java.util.Collection
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

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

    void "reads endpoint add mapping" () {
        String yaml = """\
openapi-processor-spring: v1.0
    
map:
  paths:
    /foo:
      parameters:
        - add: request
          as: javax.servlet.http.HttpServletRequest
          
        - name: bar
          to: Bar
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.typeMappings.size () == 2

        def parameter = endpoint.typeMappings.first () as AddParameterTypeMapping
        parameter.parameterName == 'request'
        parameter.mapping.sourceTypeName == null
        parameter.mapping.sourceTypeFormat == null
        parameter.mapping.targetTypeName == 'javax.servlet.http.HttpServletRequest'
        parameter.mapping.genericTypeNames == []
    }

    void "reads endpoint exclude flag" () {
        String yaml = """\
openapi-processor-spring: v1.0
    
map:
  paths:
    /foo:
      exclude: ${exclude.toString ()}
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 1

        def endpoint = mappings.first () as EndpointTypeMapping
        endpoint.path == '/foo'
        endpoint.exclude == exclude
        endpoint.typeMappings.empty

        where:
        exclude << [true, false]
    }

    void "handles empty mapping" () {
        String yaml = ""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size() == 0
    }

    void "reads global result mapping 'plain'" () {
        String yaml = """\
openapi-processor-spring: v1.0
    
map:
  result:
    to: plain
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as ResultTypeMapping
        type.targetTypeName == 'plain'
    }

    void "reads global result mapping" () {
        String yaml = """\
openapi-processor-spring: v1.0
    
map:
  result:
    to: org.springframework.http.ResponseEntity
"""

        when:
        def mapping = reader.read (yaml)
        def mappings = converter.convert (mapping)

        then:
        mappings.size () == 1
        def type = mappings.first () as ResultTypeMapping
        type.targetTypeName == 'org.springframework.http.ResponseEntity'
    }

}
