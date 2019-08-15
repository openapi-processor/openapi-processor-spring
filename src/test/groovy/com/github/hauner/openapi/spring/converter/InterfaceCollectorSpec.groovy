/*
 * Copyright 2019 https://github.com/hauner/openapi-generatr-spring
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

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.generatr.DefaultApiOptions
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem
import io.swagger.v3.oas.models.Paths
import spock.lang.Specification

class InterfaceCollectorSpec extends Specification {

    void "sets interface package from generatr options with 'api' sub package" () {
        def options = new ApiOptions(
            packageName: 'a.package.name'
        )
        def collector = new InterfaceCollector(options)

        def paths = new Paths()
        paths.put ('/path', new PathItem(get: new Operation(tags: ['any'])))

        when:
        def result = collector.collect (paths)

        then:
        result.first ().packageName == [options.packageName, 'api'].join ('.')
    }

    void "sets empty interface name when no interface name tag was provided" () {
        def collector = new InterfaceCollector(new DefaultApiOptions())

        def paths = new Paths()
        paths.put ('/path', new PathItem(get: new Operation()))

        when:
        def result = collector.collect (paths)

        then:
        result.first ().interfaceName == 'Api'
    }
}
