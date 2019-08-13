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

package com.github.hauner.openapi.generatr

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.generatr.SpringGeneratr
import groovy.io.FileType
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Unroll

class GeneratrSpec extends Specification {

    @Rule
    TemporaryFolder folder

    @Unroll
    void "generatr creates expected files for api set '#source'" () {
        def packageName = 'generated'
        def expectedPath = "./src/testInt/resources/${source}/${packageName}"
        def generatedPath = "${folder.root.absolutePath}/${packageName}"

        def generatr = new SpringGeneratr()
        def options = new ApiOptions(
            apiPath: "./src/testInt/resources/${source}/openapi.yaml",
            targetDir: folder.root,
            packageName: packageName
        )

        when:
        generatr.run (options)

        then:
        def generatedFiles = collectGenerated(generatedPath)
        def expectedFiles = collectExpected(expectedPath)
        assert generatedFiles == expectedFiles

        expectedFiles.each {
            def expected = new File([expectedPath, it].join ('/'))
            def generated = new File([generatedPath, it].join ('/'))
            assert expected.text == generated.text
        }

        where:
        source << [
            'simple'
        ]
    }

    List<String> collectGenerated(String generatedPath) {
        def generated = []
        new File(generatedPath).eachFileRecurse FileType.FILES,  {
            generated << it.absolutePath.replace (generatedPath, '')
        }
        generated
    }

    List<String> collectExpected(String expectedPath) {
        def expected = []
        new File(expectedPath).eachFileRecurse FileType.FILES,  {
            expected << it.path.replace (expectedPath, '')
        }
        expected
    }
}
