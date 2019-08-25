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

package com.github.hauner.openapi.learn

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import javax.tools.ToolProvider
import javax.tools.JavaCompiler

class CompileSpec extends Specification {

    @Rule TemporaryFolder target

    void "generated class compiles with groovy compiler" () {
        def loader = new GroovyClassLoader()
        String sourceCode = "interface Api {}"

        when:
        Class clazz = loader.parseClass(sourceCode)

        then:
        clazz
    }

    void "generated class compiles with java compiler" () {
        def pkg = 'com.github.hauner.openapi.test'
        def name = 'Test'

        File source = saveSource (pkg, name, """\
package $pkg;

interface $name {
}
 """)

        when:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler()
        compiler.run(null, null, null, source.getPath())

        then:
        def loader = new URLClassLoader([target.root.toURI ().toURL ()] as URL[])
        Class.forName("$pkg.$name", true, loader)
    }

    private File saveSource (String pkg, String name, String sourceCode) {
        def targetFolder = target.root.absolutePath

        def sourceFolder = (
            [targetFolder] + (pkg.split (/\./) as List<String>) + ["${name}.java"]
        ).join (File.separator)

        File source = new File (sourceFolder)
        source.parentFile.mkdirs ()
        source.write (sourceCode, 'UTF-8')
        source
    }
}
