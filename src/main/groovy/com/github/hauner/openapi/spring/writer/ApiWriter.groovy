package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import groovy.util.logging.Slf4j

@Slf4j
class ApiWriter {

    private ApiOptions options
    InterfaceWriter interfaceWriter

    File apiFolder
    File modelFolder

    ApiWriter(ApiOptions options, InterfaceWriter interfaceWriter) {
        this.options = options
        this.interfaceWriter = interfaceWriter
    }

    void write(Api api) {
        createTargetFolders ()

        api.interfaces.each {
            def target = new File (apiFolder, "${it.interfaceName}.java")
            def writer = new FileWriter(target)
            interfaceWriter.write (writer, it)
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
        def folder = new File ([options.targetFolder, apiPkg].join (File.separator))
        def success = folder.mkdirs ()
        if (!success) {
            log.error ('failed to create package {}', folder)
        }
        folder
    }

}
