package io.openapiprocessor

import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.artifacts.ComponentMetadataContext

class JacksonPlatformRule implements ComponentMetadataRule {
    void execute (ComponentMetadataContext ctx) {
        ctx.details.with {
            if (id.group.startsWith ("com.fasterxml.jackson")) {
                belongsTo ("com.fasterxml.jackson:jackson-bom:${id.version}", false)
            }
        }
    }
}
