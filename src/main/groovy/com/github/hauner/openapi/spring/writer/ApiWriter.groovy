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

package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.generatr.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import com.github.hauner.openapi.spring.model.datatypes.CompositeDataType
import groovy.util.logging.Slf4j

@Slf4j
class ApiWriter {

    private ApiOptions options
    InterfaceWriter interfaceWriter
    DataTypeWriter dataTypeWriter

    File apiFolder
    File modelFolder

    @Deprecated
    ApiWriter(ApiOptions options, InterfaceWriter interfaceWriter) {
        this.options = options
        this.interfaceWriter = interfaceWriter
        this.dataTypeWriter = new DataTypeWriter(headerWriter: new HeaderWriter ())
    }

    ApiWriter(ApiOptions options, InterfaceWriter interfaceWriter, DataTypeWriter dataTypeWriter) {
        this.options = options
        this.interfaceWriter = interfaceWriter
        this.dataTypeWriter = dataTypeWriter
    }

    void write(Api api) {
        createTargetFolders ()

        api.interfaces.each {
            def target = new File (apiFolder, "${it.interfaceName}.java")
            def writer = new FileWriter(target)
            interfaceWriter.write (writer, it)
            writer.close ()
        }

        api.models.each {
            def target = new File (modelFolder, "${it.type}.java")
            def writer = new FileWriter(target)
            dataTypeWriter.write (writer, it as CompositeDataType)
            writer.close ()
        }
    }

    private void createTargetFolders () {
        def rootPkg = options.packageName.replace ('.', File.separator)
        def apiPkg = [rootPkg, 'api'].join (File.separator)
        def modelPkg = [rootPkg, 'model'].join (File.separator)

        apiFolder = createTargetPackage (apiPkg)
        modelFolder = createTargetPackage (modelPkg)
    }

    private File createTargetPackage (String apiPkg) {
        def folder = new File ([options.targetDir, apiPkg].join (File.separator))
        def success = folder.mkdirs ()
        if (!success) {
            log.error ('failed to create package {}', folder)
        }
        folder
    }

}
