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

package com.github.hauner.openapi.core.processor

import com.google.common.jimfs.Configuration
import com.google.common.jimfs.Jimfs
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.FileSystem

class MappingReaderSpec extends Specification {

    @Shared
    FileSystem fs = Jimfs.newFileSystem (Configuration.unix ())

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

    void "reads mapping from url" () {
        def yaml = """\
openapi-processor-spring: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        def yamlFile = fs.getPath ('mapping.yaml')
        yamlFile.text = yaml

        when:
        def reader = new MappingReader()
        def mapping = reader.read (yamlFile.toUri ().toURL ().toString ())

        then:
        mapping
    }

    void "reads mapping from local file if the scheme is missing" () {
        def yaml = """\
openapi-processor-spring: v1.0
    
map:
  types:
    - from: array
      to: java.util.Collection
"""

        def yamlFile = folder.newFile ('mapping.yaml')
        yamlFile.text = yaml

        when:
        def reader = new MappingReader()
        def mapping = reader.read (yamlFile.toString ())

        then:
        mapping
    }

    void "reads mapping from string" () {
        def yaml = """\
openapi-processor-spring: v1.0
    
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
