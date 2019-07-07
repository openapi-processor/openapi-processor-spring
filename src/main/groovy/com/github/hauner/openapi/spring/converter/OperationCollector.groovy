package com.github.hauner.openapi.spring.converter

import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.PathItem

/**
 * collects a list of all used http methods of the given path (i.e. endpoint)
 */
class OperationCollector {
    static def methods = ['get', 'put', 'post', 'delete', 'options', 'head', 'patch', 'trace']

    List<Operation> collect (PathItem item) {
        def ops = []

        methods.each { m ->
            if (item."$m") {
                def op = item."$m" as HttpMethod // add trait
                op.httpMethod = "$m"
                ops << op
            }
        }

        ops
    }
}
