package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.ApiEndpoint

class MethodWriter {

    void write(Writer target, ApiEndpoint endpoint) {
        target.write ("    // no methods")
    }
}
