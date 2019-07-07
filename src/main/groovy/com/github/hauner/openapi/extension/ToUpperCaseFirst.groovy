package com.github.hauner.openapi.extension

trait ToUpperCaseFirst {
    String toUpperCaseFirst(String self) {
        self.substring (0, 1).toUpperCase () + self.substring (1)
    }
}

