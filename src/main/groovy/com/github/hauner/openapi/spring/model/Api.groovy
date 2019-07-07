package com.github.hauner.openapi.spring.model;

class Api {
    List<ApiInterface> interfaces = []

    ApiInterface getInterface(String name) {
        interfaces.find { it.name.toLowerCase () == name.toLowerCase () }
    }

}
