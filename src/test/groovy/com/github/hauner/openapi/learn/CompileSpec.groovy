package com.github.hauner.openapi.learn

import spock.lang.Specification

class CompileSpec extends Specification {

    void "generated class compiles" () {
        def loader = new GroovyClassLoader()
        String sourceCode = "interface Api {}"

        when:
        Class clazz = loader.parseClass(sourceCode)

        then:
        clazz
    }
}
