package io.openapiprocessor.spring.annotationprocessor

import io.openapiprocessor.core.writer.java.FileHandler
import java.io.BufferedWriter
import java.io.Writer
import javax.annotation.processing.Filer

class AnnotationProcessorFileHandler(
        private val filer: Filer
) : FileHandler {
    override fun createApiWriter(packageName: String, className: String): Writer {
        val fileObject = filer.createSourceFile("$packageName.$className")
        return BufferedWriter(fileObject.openWriter())
    }

    override fun createModelWriter(packageName: String, className: String): Writer {
        val fileObject = filer.createSourceFile("$packageName.$className")
        return BufferedWriter(fileObject.openWriter())
    }

    override fun createTargetFolders() {
    }
}