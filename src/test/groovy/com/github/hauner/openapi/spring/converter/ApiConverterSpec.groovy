package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.support.ModelAsserts
import spock.lang.Specification
import spock.lang.Unroll

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterSpec extends Specification implements ModelAsserts {

    void "groups endpoints into interfaces by first operation tag" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    get:
      tags:
        - ping
      responses:
        'default':
          description: none
  /b:
    get:
      tags:
        - pong
      responses:
        'default':
          description: none
  /c:
    get:
      tags:
        - ping
        - pong
      responses:
        'default':
          description: none
""")

        when:
        api = new ApiConverter ().convert (openApi)

        then:
        assertInterfaces ('ping', 'pong')
        assertPingEndpoints ('/a', '/c')
        assertPongEndpoints ('/b')
    }


    @Unroll
    void "groups endpoints with method #method into interfaces" () {
        def openApi = parse (
"""\
openapi: 3.0.2
info:
  title: API
  version: 1.0.0

paths:
  /a:
    ${method}:
      tags:
        - ${method}
      responses:
        'default':
          description: none
""")

        when:
        api = new ApiConverter ().convert (openApi)

        then:
        assertInterfaces (method)
        assertEndpoints (method,'/a')

        where:
        method << ['get', 'put', 'post', 'delete', 'options', 'head', 'patch', 'trace']
    }

}
