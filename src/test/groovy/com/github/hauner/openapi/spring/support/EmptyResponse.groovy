package com.github.hauner.openapi.spring.support

import com.github.hauner.openapi.spring.model.Response
import com.github.hauner.openapi.spring.model.datatypes.NoneDataType

class EmptyResponse extends Response {

    EmptyResponse() {
        contentType = null
        responseType = new NoneDataType()
    }
}
