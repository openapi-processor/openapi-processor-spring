package com.github.hauner.openapi.spring.writer

import spock.lang.Specification

class HeaderWriterSpec extends Specification {

    void "writes generated header"() {
        def headerWriter = new HeaderWriter()
        def target = new StringWriter()

        when:
        headerWriter.write (target)

        then:
        target.toString () == HeaderWriter.HEADER
    }
}
