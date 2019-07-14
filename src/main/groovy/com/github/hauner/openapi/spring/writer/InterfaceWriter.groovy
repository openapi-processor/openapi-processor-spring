package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.ApiInterface


class InterfaceWriter {
    HeaderWriter headerWriter
    MethodWriter methodWriter

    void write (Writer target, ApiInterface itf) {
        headerWriter.write (target)
        target.write ("package ${itf.packageName};\n\n")
        // todo imports
        target.write ("interface ${itf.interfaceName} {\n\n")

        itf.endpoints.each {
            methodWriter.write(target, it)
        }

        target.write ("}\n")
    }

}
