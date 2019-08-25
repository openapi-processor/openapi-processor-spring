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

package com.github.hauner.openapi.spring.support

import com.github.hauner.openapi.spring.model.Api

trait ModelAsserts {
    Api api

    void assertInterfaces(String... expected) {
        def actual = api.interfaces
        assert actual.size () == expected.size ()
        assert actual.collect { it.name } == expected.collect ()
    }

    void assertEndpoints(String interfaceName, String... endpoints) {
        def itf = api.getInterface (interfaceName)
        endpoints.each { ep ->
            assert itf.getEndpoint (ep)
        }
    }

    def methodMissing(String name, args) {
        switch (name) {
            case ~/assert(.+?)Endpoints/:
                def match = name =~ /assert(.+?)Endpoints/
                def itfName = match[0][1]
                assertEndpoints (itfName as String, args as String[])
                return void
            default:
                throw new MissingMethodException(name, delegate, args)
        }
    }
}
