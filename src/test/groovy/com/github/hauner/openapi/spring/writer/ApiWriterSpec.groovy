package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.ApiOptions
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class ApiWriterSpec extends Specification {

    @Rule TemporaryFolder target

    void "creates package structure in target folder"() {
        def opts = new ApiOptions(
            packageName: 'com.github.hauner.openapi',
            targetFolder: [target.root.toString (), 'java', 'src'].join (File.separator)
        )

        when:
        new ApiWriter (opts).write (null)

        then:
        def api = new File([opts.targetFolder, 'com', 'github', 'hauner', 'openapi', 'api'].join(File.separator))
        def model = new File([opts.targetFolder, 'com', 'github', 'hauner', 'openapi', 'model'].join(File.separator))
        api.exists ()
        api.isDirectory ()
        model.exists ()
        model.isDirectory ()
    }
}
