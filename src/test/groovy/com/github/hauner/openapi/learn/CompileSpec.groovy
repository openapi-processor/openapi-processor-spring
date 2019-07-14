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
