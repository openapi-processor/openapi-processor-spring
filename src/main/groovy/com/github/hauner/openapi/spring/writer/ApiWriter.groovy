package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.ApiOptions
import com.github.hauner.openapi.spring.model.Api
import groovy.util.logging.Slf4j

@Slf4j
class ApiWriter {

    private ApiOptions options

    File apiFolder
    File modelFolder

    ApiWriter(ApiOptions options) {
        this.options = options
    }

    void write(Api api) {
        createTargetFolders ()
    }

    private void createTargetFolders () {
        def rootPkg = options.packageName.replace ('.', File.separator)
        def apiPkg = [rootPkg, 'api'].join (File.separator)
        def modelPkg = [rootPkg, 'model'].join (File.separator)

        apiFolder = createPackage (apiPkg)
        modelFolder = createPackage (modelPkg)
    }

    private File createPackage (String apiPkg) {
        def folder = new File ([options.targetFolder, apiPkg].join (File.separator))
        def success = folder.mkdirs ()
        if (!success) {
            log.error ('failed to create package {}', folder)
        }
        folder
    }

}
