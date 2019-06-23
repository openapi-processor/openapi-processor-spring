package com.github.hauner.openapi.extension

import spock.lang.Specification

class ToUpperFirstExtensionSpec extends Specification {

    void "changes first character to upper case" () {
        expect:
        "ping".toUpperCaseFirst () == "Ping"
        "PING".toUpperCaseFirst () == "PING"
    }
}
