package com.github.hauner.openapi.spring.model

class ApiInterface {
    String pkg = 'unknown'
    String name = 'unknown'

    List<ApiEndpoint> endpoints = []

    ApiEndpoint getEndpoint(String endpoint) {
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
