package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.support.ModelAsserts
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.OpenAPIV3Parser
import spock.lang.Ignore
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



@Ignore
    void "creates model for single endpoint with single response" () {

        OpenAPI openApi = new OpenAPIV3Parser ()
            .readContents ("""\
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
""").openAPI

        when:
        Api api = new ApiConverter ().convert (openApi)

        then:
        api.interfaces.size () == 1

    }
}
