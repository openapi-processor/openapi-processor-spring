package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.ApiEndpoint

class MethodWriter {

    void write(Writer target, List<ApiEndpoint> endpoints) {
        target.write ("    // no methods")
    }
}
