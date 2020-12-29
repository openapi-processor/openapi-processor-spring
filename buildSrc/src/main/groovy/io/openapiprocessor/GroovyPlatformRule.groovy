package io.openapiprocessor

import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule

class GroovyPlatformRule implements ComponentMetadataRule {
    void execute (ComponentMetadataContext ctx) {
        ctx.details.with {
            if (id.group.startsWith ("org.codehaus.groovy")) {
                println "${id.name}, ${id.version}"
                belongsTo ("org.codehaus.groovy:groovy-platform:${id.version}")
            }
        }
    }
}
