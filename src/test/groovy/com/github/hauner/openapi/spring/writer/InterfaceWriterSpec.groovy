package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.ApiEndpoint
import com.github.hauner.openapi.spring.model.ApiInterface
import spock.lang.Specification

import java.util.stream.Collectors


class InterfaceWriterSpec extends Specification {
    def headerWriter = Mock HeaderWriter
    def methodWriter = Stub MethodWriter

    def writer = new InterfaceWriter(headerWriter: headerWriter, methodWriter: methodWriter)
    def target = new StringWriter ()

    void "writes 'generated' comment" () {
        def apiItf = new ApiInterface ()

        when:
        writer.write (target, apiItf)

        then:
        1 * headerWriter.write (target)
    }

    void "writes 'package'" () {
        def pkg = 'com.github.hauner.openapi'
        def apiItf = new ApiInterface (pkg: pkg)

        when:
        writer.write (target, apiItf)

        then:
        target.toString ().contains (
"""\
package $pkg;

""")
    }

//    void "writes 'imports'" () {
//
//    }

    void "writes 'interface' block" () {
        def apiItf = new ApiInterface (name: 'name', endpoints: [])

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
        def endpoints = [new ApiEndpoint(), new ApiEndpoint()]

        writer.methodWriter.write (_ as Writer, endpoints) >> {
            Writer target = it.get (0)
            target.write ("// none\n\n")
        }

        def apiItf = new ApiInterface (name: 'name', endpoints: endpoints)

        when:
        writer.write (target, apiItf)

        then:
        def result = extractInterfaceBody(target.toString ())
        result == """\

// none

"""
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


