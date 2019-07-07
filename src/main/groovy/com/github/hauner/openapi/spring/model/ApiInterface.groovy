package com.github.hauner.openapi.spring.model;

class ApiInterface {
    String name

    List<ApiEndpoint> endpoints = []

    ApiEndpoint getEndpoint(String endpoint) {
        endpoints.find { it.path == endpoint }
    }

    String toString () {
        name
    }
}
