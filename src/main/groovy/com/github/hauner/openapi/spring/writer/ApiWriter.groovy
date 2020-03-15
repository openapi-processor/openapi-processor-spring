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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.Interface
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType
import com.google.googlejavaformat.java.Formatter
import com.google.googlejavaformat.java.JavaFormatterOptions
import groovy.util.logging.Slf4j

import static com.github.hauner.openapi.support.Identifier.toClass

/**
 * Root writer for the generated api files.
 *
 * @author Martin Hauner
 */
@Slf4j
class ApiWriter {

    private ApiOptions options
    InterfaceWriter interfaceWriter
    DataTypeWriter dataTypeWriter
    StringEnumWriter enumWriter

    File apiFolder
    File modelFolder

    Formatter formatter

    ApiWriter (
        ApiOptions options,
        InterfaceWriter interfaceWriter,
        DataTypeWriter dataTypeWriter,
        StringEnumWriter enumWriter,
        boolean enableFormatter = true) {
        this.options = options
        this.interfaceWriter = interfaceWriter
        this.dataTypeWriter = dataTypeWriter
        this.enumWriter = enumWriter

        if (enableFormatter) {
            formatter = new Formatter (
                JavaFormatterOptions
                    .builder ()
                    .style (JavaFormatterOptions.Style.AOSP)
                    .build ())
        }
    }

    void write(Api api) {
        createTargetFolders ()

        api.interfaces.each {
            def target = new File (apiFolder, "${it.interfaceName}.java")
            def writer = new FileWriter(target)
            writeInterface (writer, it)
            writer.close ()
        }

        api.models.objectDataTypes.each {
            def target = new File (modelFolder, "${it.name}.java")
            def writer = new FileWriter(target)
            writeDataType (writer, it)
            writer.close ()
        }

        api.models.enumDataTypes.each {
            def target = new File (modelFolder, "${it.name}.java")
            def writer = new FileWriter(target)
            writeEnumDataType (writer, it)
            writer.close ()
        }
    }

    private void writeInterface (Writer writer, Interface itf) {
        def raw = new StringWriter ()
        interfaceWriter.write (raw, itf)
        writer.write (format (raw.toString ()))
    }

    private void writeDataType (Writer writer, ObjectDataType dataType) {
        def raw = new StringWriter ()
        dataTypeWriter.write (raw, dataType)
        writer.write (format (raw.toString ()))
    }

    private void writeEnumDataType (Writer writer, StringEnumDataType enumDataType) {
        def raw = new StringWriter ()
        enumWriter.write (raw, enumDataType)
        writer.write (format (raw.toString ()))
    }

    private String format (String raw) {
        if (formatter == null) {
            return raw
        }
        correctLineFeed (formatter.formatSource (raw))
    }

    private String correctLineFeed (String formatted) {
        int index = formatted.findLastIndexOf (0) {
            it == '}'
        }

        new StringBuilder ()
            .append (formatted.substring (0, index))
            .append ("\n}\n")
    }

    private void createTargetFolders () {
        def rootPkg = options.packageName.replace ('.', File.separator)
        def apiPkg = [rootPkg, 'api'].join (File.separator)
        def modelPkg = [rootPkg, 'model'].join (File.separator)
        log.debug ('creating target folders: {}', rootPkg)

        apiFolder = createTargetPackage (apiPkg)
        log.debug ('created target folder: {}', apiFolder.absolutePath)

        modelFolder = createTargetPackage (modelPkg)
        log.debug ('created target folder: {}', modelFolder.absolutePath)
    }

    private File createTargetPackage (String apiPkg) {
        def folder = new File ([options.targetDir, apiPkg].join (File.separator))
        if (folder.exists () && folder.isDirectory ()) {
            return folder
        }

        def success = folder.mkdirs ()
        if (!success) {
            log.error ('failed to create package {}', folder)
        }
        folder
    }

}
