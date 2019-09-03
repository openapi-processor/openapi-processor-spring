package com.github.hauner.openapi.generatr

import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.generatr.SpringGeneratr
import groovy.io.FileType
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder;


import static org.junit.Assert.assertEquals;

public class GeneratrTestBase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    String source

    GeneratrTestBase(String source) {
        this.source = source
    }

    @Test
    void "generatr creates expected files for api set "() {
        def packageName = 'generated'
        def expectedPath = ['.', 'src', 'testInt', 'resources', source, packageName].join(File.separator)
        def generatedPath = [folder.root.absolutePath, packageName].join(File.separator)

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

            printUnifiedDiff (expected, generated)
            assertEquals(
                // ignore cr (ie. crlf vs lf)
                expected.text.replace('\r',''),
                generated.text.replace('\r','')
            )
        }
    }

    void printUnifiedDiff (File expected, File generated) {
        def patch = DiffUtils.diff (
            expected.readLines (),
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            expected.path,
            generated.path,
            expected.readLines (),
            patch,
            2
        )

        diff.each {
            println it
        }
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
