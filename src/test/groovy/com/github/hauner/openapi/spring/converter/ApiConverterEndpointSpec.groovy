package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.support.ModelAsserts
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterEndpointSpec extends Specification implements ModelAsserts {

    void "creates model for an endpoint without parameters and a single response content type" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: Ping API
  version: 1.0.0

paths:
  /ping:
    get:
      tags:
        - ping
      responses:
        '200':
          description: string result
          content:
            text/plain:
              schema:
                type: string
""")

        when:
        Api api = new ApiConverter ().convert (openApi)

        then:
        api.interfaces.size () == 1
        api.interfaces.get(0).endpoints.size () == 1

        and:
        def itf = api.interfaces.get (0)
        def ep = itf.endpoints.get(0)
        ep.path == '/ping'
        ep.method == 'get'
        ep.response.contentType == 'text/plain'
        ep.response.responseType.type == 'string'
    }

}
