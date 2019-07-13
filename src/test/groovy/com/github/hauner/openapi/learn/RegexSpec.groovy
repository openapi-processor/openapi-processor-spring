package com.github.hauner.openapi.learn

import spock.lang.Specification

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegexSpec extends Specification {

    void "regex group with java" () {
        Pattern p = Pattern.compile("has(.+?)Endpoints")
        Matcher m = p.matcher("hasPingEndpoints")

        expect:
        m.matches()
        "Ping" == m.group(1)
    }

    void "regex group with groovy ~ operator" () {
        Pattern p = ~/has(.+?)Endpoints/
        Matcher m = "hasPingEndpoints" =~ p

        expect:
        m instanceof Matcher
        m.find ()
        m.group (1) == "Ping"
    }

    void "match regex to boolean" () {
        expect:
        "has_XXXX_Endpoints" ==~ /has(.+?)Endpoints/
    }

    void "match line feeds with 'single line mode'"() {
        expect:
        "prefix\nXXXX\npostfix" ==~ /(?s)prefix(.+?)postfix/
    }
}
