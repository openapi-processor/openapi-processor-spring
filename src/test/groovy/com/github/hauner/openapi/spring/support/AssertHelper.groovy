package com.github.hauner.openapi.spring.support

import java.util.stream.Collectors

class AssertHelper {

    static String extractImports (String source) {
        source.readLines ().stream ()
            .filter {it.startsWith ('import ')}
            .collect (Collectors.toList ())
            .join ('\n') + '\n'
    }

}
