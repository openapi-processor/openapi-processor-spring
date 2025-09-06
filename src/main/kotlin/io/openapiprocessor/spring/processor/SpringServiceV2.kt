/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

//@file:Suppress("DEPRECATION")

package io.openapiprocessor.spring.processor

import io.openapiprocessor.api.v2.Version
import io.openapiprocessor.core.version.GitHubVersionException
import io.openapiprocessor.core.version.GitHubVersionProvider
import io.openapiprocessor.test.api.OpenApiProcessorTest
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *  Entry point of openapi-processor-spring loaded via [java.util.ServiceLoader]. by the v2 interface
 *  [io.openapiprocessor.api.v1.OpenApiProcessor].
 *
 *  the v2 interfaces *must* be implemented by its own class and not by [SpringService] to be downward
 *  compatible with gradle/maven plugin versions that do not know the v2 interfaces.
 */
class SpringServiceV2(
    private val provider: GitHubVersionProvider = GitHubVersionProvider("openapi-processor-spring"),
    private val testMode: Boolean = false):
    io.openapiprocessor.api.v2.OpenApiProcessor,
    io.openapiprocessor.api.v2.OpenApiProcessorVersion,
    OpenApiProcessorTest
{
    private val log: Logger = LoggerFactory.getLogger(this.javaClass.name)

    private var sourceRoot: String? = null
    private var resourceRoot: String? = null

    override fun getName(): String {
        return "spring"
    }

    override fun run(processorOptions: MutableMap<String, *>) {
        try {
            val processor = SpringProcessor()
            if (testMode) {
                processor.enableTestMode()
            }
            processor.run(processorOptions)

            sourceRoot = processor.sourceRoot
            resourceRoot = processor.resourceRoot
        } catch (ex: Exception) {
            throw ex
        }
    }

    override fun getVersion(): String {
        return io.openapiprocessor.spring.Versions.version
    }

    override fun getLatestVersion(): Version {
        return provider.getVersion()
    }

    override fun hasNewerVersion(): Boolean {
        try {
            val version = version
            val latest = latestVersion

            if (latest.name > version) {
                log.info("openapi-processor-spring version ${latest.name} is available! I'm version ${version}.")
                return true
            }

            return false
        } catch (ex: GitHubVersionException) {
            // just ignore, do not complain
            return false
        }
    }

    override fun getSourceRoot(): String? {
        return sourceRoot
    }

    override fun getResourceRoot(): String? {
        return resourceRoot
    }
}
