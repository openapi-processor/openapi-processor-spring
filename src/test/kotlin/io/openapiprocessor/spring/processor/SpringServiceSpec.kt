/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.openapiprocessor.core.version.GitHubVersion
import io.openapiprocessor.core.version.GitHubVersionProvider
import io.openapiprocessor.spring.Versions
import java.time.Instant

class SpringServiceSpec : StringSpec({

    "get version" {
        val service = SpringServiceV2()
        service.version.shouldBe(Versions.version)
    }

    "gets latest version" {
        val provider = mockk<GitHubVersionProvider>()
        every { provider.getVersion() } returns GitHubVersion("1", Instant.now(), "any")

        val service = SpringServiceV2(provider)

        service.latestVersion.name shouldBe "1"
    }

    "checks newer version available" {
        val provider = mockk<GitHubVersionProvider>()
        every { provider.getVersion() } returns GitHubVersion("3000", Instant.now(), "any")

        val service = SpringServiceV2(provider)
        service.hasNewerVersion().shouldBeTrue()
    }

    "checks newer version not available" {
        val provider = mockk<GitHubVersionProvider>()
        every { provider.getVersion() } returns GitHubVersion(Versions.version, Instant.now(), "any")

        val service = SpringServiceV2(provider)
        service.hasNewerVersion().shouldBeFalse()
    }

})
