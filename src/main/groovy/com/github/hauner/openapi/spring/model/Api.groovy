package com.github.hauner.openapi.spring.model;

class Api {
    List<Interface> interfaces = []

    Interface getInterface(String name) {
        interfaces.find { it.name.toLowerCase () == name.toLowerCase () }
    }

}
