package com.github.hauner.openapi.spring.writer

import com.github.hauner.openapi.spring.model.Endpoint

class MethodWriter {

    void write(Writer target, Endpoint endpoint) {
        target.write ("    // no methods")
    }
}
