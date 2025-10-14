import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

abstract class NewTestApiTask : DefaultTask() {

    @get:InputDirectory
    abstract val srcDir: DirectoryProperty

    @get:Input
    abstract val srcVersion: Property<String>

    @get:Input
    abstract val dstVersion: Property<String>

    @get:Input
    abstract val apiVersion: Property<String>

    @TaskAction
    fun run() {
        val yamlFactory = YAMLFactory.builder()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
            .build()

        val mapper = ObjectMapper(yamlFactory).registerKotlinModule()

        val tests = srcDir.get().asFile
        val testDirs = tests.listFiles()
            ?.sorted()
            ?.toList()
            ?.filter { it.isDirectory }
            ?: emptyList()

        for (testDir in testDirs) {
            println("checking 'tests/${testDir.name}' ...")
            val srcFile = File(testDir, "inputs/openapi${srcVersion.get()}.yaml")
            val dstFile = File(testDir, "inputs/openapi${dstVersion.get()}.yaml")

            if(!srcFile.exists()) {
                println("no '${srcFile.name}' ...")
                continue
            }

            if (!dstFile.exists()) {
                println("creating '${dstFile.name}' ...")
                srcFile.copyTo(dstFile)
            }

            val apiYaml = mapper.readValue(dstFile, object : TypeReference<MutableMap<String, Any?>>() {})
            if (apiYaml["openapi"] != apiVersion.get()) {
                println("updating '${dstFile.name}' ...")
                apiYaml["openapi"] = apiVersion.get()
                mapper.writeValue(dstFile, apiYaml)
            }

            val inputsFile = File(testDir, "inputs.yaml")
            val inputsYaml = mapper.readValue(inputsFile, object : TypeReference<MutableMap<String, Any?>>() {})
            @Suppress("UNCHECKED_CAST") val items = inputsYaml["items"] as MutableList<String>
            if (!items.contains("inputs/openapi${dstVersion.get()}.yaml")) {
                println("updating inputs.yaml ...")
                items.indexOf("inputs/openapi${srcVersion.get()}.yaml").let { index ->
                    items.add(index + 1, "inputs/openapi${dstVersion.get()}.yaml")
                }
                mapper.writeValue(inputsFile, inputsYaml)
            }
        }
    }
}

tasks.register<NewTestApiTask>("newTestApi") {
    group = "utility"
    description = "setup new openapiXX.yaml test files."

    srcDir = layout.projectDirectory.dir("src/testInt/resources/tests")
    srcVersion = "31"
    dstVersion = "32"
    apiVersion = "3.2.0"
}
