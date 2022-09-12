package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.support.toURI
import io.openapiprocessor.core.writer.java.FileHandler
import io.openapiprocessor.core.writer.java.PathWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedWriter
import java.io.Writer
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class GradlePluginFileHandler(
        private val options: ApiOptions
) : FileHandler {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)
    private lateinit var apiFolder: Path
    private lateinit var modelFolder: Path

    override fun createApiWriter(packageName: String, className: String): Writer {
        val target = apiFolder.resolve("${className}.java")
        return BufferedWriter(PathWriter(target))
    }

    override fun createModelWriter(packageName: String, className: String): Writer {
        val target = modelFolder.resolve("${className}.java")
        return BufferedWriter(PathWriter(target))
    }

    override fun createTargetFolders() {
        val rootPkg = options.packageName.replace(".", "/")
        val apiPkg = listOf(rootPkg, "api").joinToString("/")
        val modelPkg = listOf(rootPkg, "model").joinToString("/")

        apiFolder = createTargetPackage(apiPkg)
        log.debug("created target folder: {}", apiFolder.toAbsolutePath().toString())

        modelFolder = createTargetPackage(modelPkg)
        log.debug("created target folder: {}", modelFolder.toAbsolutePath().toString())
    }

    private fun createTargetPackage(apiPkg: String): Path {
        val root = options.targetDir
        val pkg = listOf(root, apiPkg).joinToString("/")

        val target = Paths.get (toURI(pkg))
        Files.createDirectories(target)
        return target
    }
}