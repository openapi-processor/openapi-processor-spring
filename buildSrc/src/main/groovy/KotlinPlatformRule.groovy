import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule

class KotlinPlatformRule implements ComponentMetadataRule {
    void execute (ComponentMetadataContext ctx) {
        ctx.details.with {
            if (id.group.startsWith ("org.jetbrains.kotlin")) {
                belongsTo ("org.jetbrains.kotlin:kotlin-platform:${id.version}")
            }
        }
    }
}
