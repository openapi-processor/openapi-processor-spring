/*
 * Copyright 2019-2020 the original authors
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

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import spock.lang.Specification
import spock.lang.Subject

import static com.github.hauner.openapi.spring.support.OpenApiParser.parse

class ApiConverterErrorSpec extends Specification {

    @Subject
    def converter

    def appender

    void setup () {
        converter = new ApiConverter ()

        appender = new ListAppender<ILoggingEvent> ()
        appender.start ()
        converter.log.addAppender (appender)
    }

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
        converter.convert (openApi)

        then:
        notThrown (UnknownDataTypeException)
        appender.list.size () == 1
        appender.list.first ().level == Level.ERROR
    }

}
