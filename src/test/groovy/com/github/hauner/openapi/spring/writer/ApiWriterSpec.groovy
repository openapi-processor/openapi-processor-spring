/*
 * Copyright 2019 https://github.com/hauner/openapi-spring-generatr
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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.Interface
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ApiWriterSpec extends Specification {

    @Rule TemporaryFolder target

    List<String> apiPkgPath = ['com', 'github', 'hauner', 'openapi', 'api']

    void "creates package structure in target folder"() {
        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        when:
        new ApiWriter (opts, Stub (InterfaceWriter)).write (new Api())

        then:
        def api = new File([opts.targetDir, 'com', 'github', 'hauner', 'openapi', 'api'].join(File.separator))
        def model = new File([opts.targetDir, 'com', 'github', 'hauner', 'openapi', 'model'].join(File.separator))
        api.exists ()
        api.isDirectory ()
        model.exists ()
        model.isDirectory ()
    }

    void "generates interface sources in api target folder"() {
        def interfaceWriter = Stub (InterfaceWriter) {
            write (_ as Writer, _ as Interface) >> {
                Writer writer = it.get(0)
                writer.write ('Foo interface!\n')
            } >> {
                Writer writer = it.get(0)
                writer.write ('Bar interface!\n')
            }
        }

        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetDir: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        def api = new Api(interfaces: [
            new Interface(pkg: "${opts.packageName}.api", name: 'Foo'),
            new Interface(pkg: "${opts.packageName}.api", name: 'Bar')
        ])

        when:
        new ApiWriter (opts, interfaceWriter).write (api)

        then:
        def fooSource = new File(getApiPath (opts.targetDir, 'FooApi.java'))
        fooSource.text == """\
Foo interface!
"""
        def barSource = new File(getApiPath (opts.targetDir, 'BarApi.java'))
        barSource.text == """\
Bar interface!
"""
    }

    String getApiPath(String targetFolder, String clazzName) {
        ([targetFolder] + apiPkgPath + [clazzName]).join(File.separator)
    }
}
