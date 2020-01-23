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

package com.github.hauner.openapi.spring.generatr

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class MappingReaderSpec extends Specification {

    @Rule
    TemporaryFolder folder

    void "ignores empty type mapping" () {
        when:
        def reader = new MappingReader()
        def mapping = reader.read (yaml)

        then:
        mapping == null

        where:
        yaml << [null, ""]
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
        def reader = new MappingReader()
        def mapping = reader.read (yamlFile.absolutePath)

        then:
        mapping
    }

    void "reads mapping from string" () {
        def yaml = """\
openapi-generatr-spring: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        when:
        def reader = new MappingReader()
        def mapping = reader.read (yaml)

        then:
        mapping
    }

}
