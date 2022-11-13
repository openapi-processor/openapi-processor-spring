/*
 * Copyright 2022 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.annotations.SpringApi
import io.openapiprocessor.annotations.SpringApis
import io.openapiprocessor.core.writer.*
import java.nio.file.Paths
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.io.path.notExists

/**
 *  Entry point of openapi-processor-spring annotation processor.
 */
@SupportedOptions("io.openapiprocessor.project.root")
class SpringAnnotationProcessor: AbstractProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val writerFactory = FilerWriterFactory(processingEnv.filer)

        try {
            roundEnv.getElementsAnnotatedWith(SpringApi::class.java).forEach { el ->
                val annotation = el.getAnnotation(SpringApi::class.java)
                val processorOptions = initProcessorOptions(annotation)
                SpringProcessor(writerFactory).run(processorOptions)
            }

            roundEnv.getElementsAnnotatedWith(SpringApis::class.java).forEach { el ->
                for (annotation in el.getAnnotation(SpringApis::class.java).value) {
                    val processorOptions = initProcessorOptions(annotation)
                    SpringProcessor(writerFactory).run(processorOptions)
                }
            }
        } catch (ex: ProcessingException) {
           error(ex.message)
        }
        finally {
        }
        return true
    }

    private fun initProcessorOptions(annotation: SpringApi): MutableMap<String, Any> {
        val processorOptions = mutableMapOf<String, Any>()

        val projectRootPath = Paths.get(projectRoot)
        if (projectRootPath.notExists()) {
            throw MissingPathException(projectRoot)
        }

        val apiPath = projectRootPath.resolve(annotation.apiPath)
        if (apiPath.notExists()) {
            throw MissingPathException(apiPath.toString())
        }
        processorOptions["apiPath"] = apiPath.toUri().toString()

        val mappingPath = projectRootPath.resolve(annotation.mapping)
        if (mappingPath.notExists()) {
            throw MissingPathException(mappingPath.toString())
        }
        processorOptions["mapping"] = mappingPath.toUri().toString()

        processorOptions["parser"] = annotation.parser
        processorOptions["targetDir"] = "this-should-not-be-used"

        return processorOptions
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            SpringApi::class.java.canonicalName,
            SpringApis::class.java.canonicalName)
    }

    private val projectRoot: String
        get() = processingEnv.options[PROJECT_ROOT] ?: throw MissingOptionException(PROJECT_ROOT)

    private fun error(message: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, message)
    }
}
