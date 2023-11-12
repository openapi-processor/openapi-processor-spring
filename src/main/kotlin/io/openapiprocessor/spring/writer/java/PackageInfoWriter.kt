/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import java.io.Writer

class PackageInfoWriter(val options: ApiOptions) {

    fun writePackageInfo(target: Writer) {
        target.write("""
            @org.springframework.lang.NonNullApi
            @org.springframework.lang.NonNullFields
            package ${options.packageName}.spring;
            """.trimIndent())
    }
}
