/*
 * Copyright 2023 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.writer.java

import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.writer.SourceFormatter
import io.openapiprocessor.core.writer.WriterFactory
import java.io.StringWriter
import java.io.Writer

class AdditionalEnumWriter {

    fun write(options: ApiOptions, formatter: SourceFormatter, factory: WriterFactory) {
        if (options.enumType != "framework") {
            return
        }

        val piWriter = createPackageInfoWriter(options, factory)
        writePackageInfo(options, piWriter, formatter)
        piWriter.close()

        val writer = createFactoryWriter(options, factory)
        writeEnumConverterFactory(options, writer, formatter)
        writer.close()
    }

    private fun writePackageInfo(options: ApiOptions, writer: Writer, formatter: SourceFormatter) {
        val raw = StringWriter()
        PackageInfoWriter(options).writePackageInfo(raw)
        writer.write(formatter.format(raw.toString()))
    }

    private fun writeEnumConverterFactory(options: ApiOptions, writer: Writer, formatter: SourceFormatter) {
        val raw = StringWriter()
        EnumConverterFactoryWriter(options).writeConverterFactory(raw)
        writer.write(formatter.format(raw.toString()))
    }

    private fun createFactoryWriter(options: ApiOptions, writerFactory: WriterFactory): Writer {
        return writerFactory.createWriter(
            "${options.packageName}.spring",
            "StringToEnumConverterFactory")
    }

    private fun createPackageInfoWriter(options: ApiOptions, writerFactory: WriterFactory): Writer {
        return writerFactory.createWriter(
            "${options.packageName}.spring",
            "package-info")
    }
}
