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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.datatypes.CompositeDataType
import com.github.hauner.openapi.spring.model.datatypes.StringDataType
import spock.lang.PendingFeature
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.AssertHelper.extractImports

class DataTypeWriterSpec extends Specification {
    def headerWriter = Mock HeaderWriter

    def writer = new DataTypeWriter(headerWriter: headerWriter)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def dataType = new CompositeDataType(type: 'Book', properties: [:])

        when:
        writer.write (target, dataType)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new CompositeDataType (type: 'Book', properties: [:], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains (
"""\
package $pkg;

""")
    }

    void "writes imports of 'external' types" () {
        def pkg = 'external'

        def dataType = new CompositeDataType (type: 'Book', properties: [
            'isbn': new CompositeDataType (type: 'Isbn', properties: [:], pkg: pkg)
        ])

        when:
        writer.write (target, dataType)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import external.Isbn;
""")
    }

//    @PendingFeature
    void "writes properties"() {
        def pkg = 'com.github.hauner.openapi'
        def dataType = new CompositeDataType (type: 'Book', properties: [
            isbn: new StringDataType(),
            title: new StringDataType ()
        ], pkg: pkg)

        when:
        writer.write (target, dataType)

        then:
        target.toString ().contains ("""\
    private String isbn;
    private String title;
""")
    }
}
