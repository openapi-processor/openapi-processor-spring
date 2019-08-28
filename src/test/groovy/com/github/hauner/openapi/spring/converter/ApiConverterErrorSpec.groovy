/*
 * Copyright 2019 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.hauner.openapi.spring.converter

import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.support.Sl4jMockRule
import org.junit.Rule
import org.slf4j.Logger
import spock.lang.Specification

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterErrorSpec extends Specification {

    def log = Mock Logger
    @Rule Sl4jMockRule rule = new Sl4jMockRule(ApiConverter, log)

    void "logs error when datatype conversion fails" () {
        def openApi = parse ("""\
openapi: 3.0.2
info:
  title: unknown data type
  version: 1.0.0

paths:
  /book:
    get:
      responses:
        '200':
          description: none
          content:
            text/plain:
              schema:
                type: unknown
""")
        when:
        log.isErrorEnabled () >> true
        Api api = new ApiConverter ().convert (openApi)

        then:
        notThrown (UnknownDataTypeException)
        1 * log.error (*_)
    }

}
