/*
 * Copyright 2019 https://github.com/hauner/openapi-spring-generatr
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

import com.github.hauner.openapi.spring.model.Endpoint
import com.github.hauner.openapi.spring.model.HttpMethod
import com.github.hauner.openapi.spring.model.Interface
import com.github.hauner.openapi.spring.model.Response
import spock.lang.Specification

import java.util.stream.Collectors


class InterfaceWriterSpec extends Specification {
    def headerWriter = Mock HeaderWriter
    def methodWriter = Stub MethodWriter

    def writer = new InterfaceWriter(headerWriter: headerWriter, methodWriter: methodWriter)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def apiItf = new Interface ()

        when:
        writer.write (target, apiItf)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def apiItf = new Interface (pkg: pkg)

        when:
        writer.write (target, apiItf)

        then:
        target.toString ().contains (
"""\
package $pkg;

""")
    }

    void "writes GetMapping import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET)
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.web.bind.annotation.GetMapping;
""")
    }

    void "writes ResponseEntity import" () {
        def apiItf = new Interface (name: 'name', endpoints: [
            new Endpoint(path: 'path', method: HttpMethod.GET)
        ])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractImports (target.toString ())
        result.contains("""\
import org.springframework.http.ResponseEntity;
""")
    }

    void "writes 'interface' block" () {
        def apiItf = new Interface (name: 'name', endpoints: [])

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBlock(target.toString ())
        result == """\
interface NameApi {
}
"""
    }

    void "writes methods" () {
        def endpoints = [
            new Endpoint(path: 'path1', method: HttpMethod.GET),
            new Endpoint(path: 'path2', method: HttpMethod.GET)
        ]

        writer.methodWriter.write (_ as Writer, _ as Endpoint) >> {
            Writer target = it.get (0)
            Endpoint e = it.get (1)
            target.write ("// ${e.path}\n\n")
        }

        def apiItf = new Interface (name: 'name', endpoints: endpoints)

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBody(target.toString ())
        result == """\

// path1

// path2

"""
    }


    String extractImports (String source) {
        source.lines ()
            .filter {it.startsWith ('import ')}
            .collect (Collectors.toList ())
            .join ('\n') + '\n'
    }

    String extractInterfaceBlock (String source) {
        source.lines ()
            .filter {it ==~ /interface (.+?) \{/ || it ==~ /\}/}
            .collect (Collectors.toList ())
            .join ('\n') + '\n'
    }

    String extractInterfaceBody (String source) {
        source
            .replaceFirst (/(?s)(.*?)interface (.+?) \{\n/, '')
            .replaceFirst (/(?s)\}\n/, '')
    }
}


