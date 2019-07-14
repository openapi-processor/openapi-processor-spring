package com.github.hauner.openapi.spring.model

class Interface {
    String pkg = 'unknown'
    String name = 'unknown'

    List<Endpoint> endpoints = []

    Endpoint getEndpoint(String endpoint) {
        endpoints.find { it.path == endpoint }
    }

    String getPackageName() {
        pkg
    }

    String getInterfaceName() {
        name.toUpperCaseFirst () + "Api"
    }

    String toString () {
        "$pkg.$name"
    }
}
