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

package com.github.hauner.openapi.processor

import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.hauner.openapi.spring.processor.MappingReader
import com.github.hauner.openapi.spring.processor.mapping.VersionedMapping
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.util.stream.Stream

import static org.junit.Assert.assertEquals

abstract class ProcessorTestBase {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    Path project = Path.of ("").toAbsolutePath ()
    TestSet testSet

    ProcessorTestBase (TestSet testSet) {
        this.testSet = testSet
    }

    protected runOnNativeFileSystem () {
        def source = testSet.name

        def processor = testSet.processor
        def options = [
            parser: testSet.parser.toString (),
            apiPath: "${project.toString ()}/src/testInt/resources/${source}/openapi.yaml",
            targetDir: folder.root
        ]

        def mappingYaml = project.resolve ("src/testInt/resources/${source}/mapping.yaml")
        if(Files.exists (mappingYaml)) {
            options.mapping = mappingYaml.toString ()
        } else {
            options.mapping = testSet.defaultOptions
        }

        def reader = new MappingReader ()
        VersionedMapping mapping = reader.read (options.mapping as String)

        def packageName = mapping.options.packageName
        def expectedPath = project.resolve ("src/testInt/resources/${source}/${packageName}")
        def generatedPath = Path.of (folder.root.toString()).resolve (packageName)

        when:
        processor.run (options)

        then:
        def expectedFiles = collectPaths (expectedPath)
        def generatedFiles = collectPaths (generatedPath)
        assert expectedFiles == generatedFiles

        expectedFiles.each {
            def expected = expectedPath.resolve (it)
            def generated = generatedPath.resolve (it)

            printUnifiedDiff (expected, generated)
            assertEquals(
                // ignore cr (ie. crlf vs lf)
                expected.text.replace('\r',''),
                generated.text.replace('\r','')
            )
        }
    }

    protected void runOnCustomFileSystem (FileSystem fs) {
        def source = testSet.name

        Path root = Files.createDirectory (fs.getPath ("source"))
        Path files = Path.of ("./src/testInt/resources/${source}")
        copy (files, root)

        Path api = root.resolve ('openapi.yaml')
        Path target = fs.getPath ('target')

        def processor = testSet.processor
        def options = [
            parser: 'OPENAPI4J', // swagger-parser does not work with fs
            apiPath: api.toUri ().toURL ().toString (),
            targetDir: target.toUri ().toURL ().toString ()
        ]

        def mappingYaml = root.resolve ('mapping.yaml')
        if(Files.exists (mappingYaml)) {
            options.mapping = mappingYaml.toUri ().toURL ().toString ()
        } else {
            options.mapping = testSet.defaultOptions
        }

        def reader = new MappingReader ()
        VersionedMapping mapping = reader.read (options.mapping as String)

        def packageName = mapping.options.packageName
        def expectedPath = root.resolve (packageName)
        def generatedPath = target.resolve (packageName)

        when:
        processor.run (options)

        then:
        def expectedFiles = collectPaths (expectedPath)
        def generatedFiles = collectPaths (generatedPath)
        assert expectedFiles == generatedFiles

        expectedFiles.each {
            def expected = expectedPath.resolve (it)
            def generated = generatedPath.resolve (it)

            printUnifiedDiff (expected, generated)
            assertEquals(
                // ignore cr (ie. crlf vs lf)
                expected.text.replace('\r',''),
                generated.text.replace('\r','')
            )
        }
    }

    private void copy (Path source, Path target) {
        Stream<Path> paths = Files.walk (source)
            .filter ({f -> !Files.isDirectory (f)})

        paths.forEach { p ->
            Path relativePath = source.relativize (p)
            Path targetPath = target.resolve (relativePath.toString ())
            Files.createDirectories (targetPath.getParent ())

            InputStream src = Files.newInputStream (p)
            OutputStream dst = Files.newOutputStream (targetPath)
            src.transferTo (dst)
        }

        paths.close ()
    }

    private List<String> collectPaths(Path source) {
        def files = []

        def found = Files.walk (source)
            .filter ({ f ->
                !Files.isDirectory (f)
            })

        found.forEach ({f ->
                files << source.relativize (f).toString ()
            })
        found.close ()

        return files
    }

    void printUnifiedDiff (Path expected, Path generated) {
        def patch = DiffUtils.diff (
            expected.readLines (),
            generated.readLines ())

        def diff = UnifiedDiffUtils.generateUnifiedDiff (
            expected.toString (),
            generated.toString (),
            expected.readLines (),
            patch,
            2
        )

        diff.each {
            println it
        }
    }

}
