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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.converter.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.datatypes.ObjectDataType
import com.github.hauner.openapi.spring.model.datatypes.StringEnumDataType
import groovy.util.logging.Slf4j

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

    @Deprecated
    ApiWriter(ApiOptions options, InterfaceWriter interfaceWriter) {
        this.options = options
        this.interfaceWriter = interfaceWriter
        this.dataTypeWriter = new DataTypeWriter(headerWriter: new HeaderWriter ())
        this.enumWriter = new StringEnumWriter()
    }

    ApiWriter(ApiOptions options,
              InterfaceWriter interfaceWriter,
              DataTypeWriter dataTypeWriter,
              StringEnumWriter enumWriter) {
        this.options = options
        this.interfaceWriter = interfaceWriter
        this.dataTypeWriter = dataTypeWriter
        this.enumWriter = enumWriter
    }

    void write(Api api) {
        createTargetFolders ()

        api.interfaces.each {
            def target = new File (apiFolder, "${it.interfaceName}.java")
            def writer = new FileWriter(target)
            interfaceWriter.write (writer, it)
            writer.close ()
        }

        api.models.objectDataTypes.each {
            def target = new File (modelFolder, "${it.name}.java")
            def writer = new FileWriter(target)
            dataTypeWriter.write (writer, it as ObjectDataType)
            writer.close ()
        }

        api.models.enumDataTypes.each {
            def target = new File (modelFolder, "${it.name}.java")
            def writer = new FileWriter(target)
            enumWriter.write (writer, it as StringEnumDataType)
            writer.close ()
        }
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
