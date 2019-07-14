package com.github.hauner.openapi.spring.model;

public class Endpoint {
    String path
    String method

    List<Response> responses = []

    Response getResponse() {
        responses.get(0)
    }
}
