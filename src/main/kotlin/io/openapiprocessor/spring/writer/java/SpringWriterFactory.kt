/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.writer.DefaultWriterFactory
import java.nio.file.Path

class SpringWriterFactory(options: ApiOptions): DefaultWriterFactory(options) {

    override fun initAdditionalPackages(options: ApiOptions): Map<String, Path> {
        val pkgPaths = HashMap<String, Path>()

        if (options.enumType == "framework") {
            val (name, path) = initTargetPackage("spring")
            pkgPaths[name] = path
        }

        return pkgPaths
    }
}
