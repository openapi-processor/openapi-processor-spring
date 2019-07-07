package com.github.hauner.openapi.spring.support

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.parser.OpenAPIV3Parser

class OpenApiParser {
    static OpenAPI parse(String apiYaml, showWarnings = true) {
        def contents = new OpenAPIV3Parser ().readContents (apiYaml)

        if (showWarnings) {
            printWarnings(contents.messages)
        }

        contents.openAPI
    }

    private static printWarnings(List<String> warnings) {
        if (warnings.empty) {
            return
        }

        println "OpenAPI warnings:"
        warnings.each {
            println it
        }
    }
}
