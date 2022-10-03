package io.openapiprocessor.spring.annotationprocessor

import io.openapiprocessor.core.converter.ApiConverter
import io.openapiprocessor.core.converter.ApiOptions
import io.openapiprocessor.core.converter.OptionsConverter
import io.openapiprocessor.core.parser.Parser
import io.openapiprocessor.core.writer.java.*
import io.openapiprocessor.spring.processor.SpringFramework
import io.openapiprocessor.spring.processor.SpringFrameworkAnnotations
import io.openapiprocessor.spring.writer.java.HeaderWriter
import io.openapiprocessor.spring.writer.java.MappingAnnotationWriter
import io.openapiprocessor.spring.writer.java.ParameterAnnotationWriter
import java.lang.RuntimeException
import java.nio.file.Paths
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Filer
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import kotlin.io.path.notExists

class SpringAnnotationProcessor : AbstractProcessor() {
    companion object {
        @JvmStatic
        val OPTION_KEY_ROOT_PATH = "io.openapiprocessor.project.root"
    }

    private lateinit var filer: Filer

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        this.filer = processingEnv.filer
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
                OpenAPISpringProcessor::class.java.canonicalName
        )
    }

    override fun getSupportedOptions(): MutableSet<String> {
        return mutableSetOf(
                OPTION_KEY_ROOT_PATH
        )
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        val annotatedElements = roundEnv.getElementsAnnotatedWith(OpenAPISpringProcessor::class.java)

        val rootPathString = processingEnv.options[OPTION_KEY_ROOT_PATH]
                ?: throw RuntimeException("ROOT PATH IS NOT SET")

        val rootPath = Paths.get(rootPathString)

        if (rootPath.notExists()) {
            throw RuntimeException("ROOT PATH NOT EXISTS")
        }

        for (annotatedElement in annotatedElements) {
            val annotation = annotatedElement.getAnnotation(OpenAPISpringProcessor::class.java)

            val processorOptions = HashMap<String, Any>()
            val apiPath = rootPath.resolve(annotation.apiPath)
            if (apiPath.notExists()) {
                throw RuntimeException("ROOT PATH NOT EXISTS")
            }
            processorOptions["apiPath"] = apiPath.toUri().toString()

            if (annotation.mapping.isNotEmpty()) {
                val mappingPath = rootPath.resolve(annotation.mapping)
                if (mappingPath.notExists()) {
                    throw RuntimeException("ROOT PATH NOT EXISTS")
                }
                processorOptions["mapping"] = mappingPath.toUri().toString()
            }

            try {
                val parser = Parser()
                val openapi = parser.parse(processorOptions)
                if (processorOptions["showWarnings"] != null) {
                    openapi.printWarnings()
                }

                val framework = SpringFramework()
                val frameworkAnnotations = SpringFrameworkAnnotations()

                val options = convertOptions(processorOptions)
                val cv = ApiConverter(options, framework)
                val api = cv.convert(openapi)

                val headerWriter = HeaderWriter()
                val beanValidationFactory = BeanValidationFactory()
                val javaDocWriter = JavaDocWriter()

                val writer = ApiWriter(
                        options,
                        InterfaceWriter(
                                options,
                                headerWriter,
                                MethodWriter(
                                        options,
                                        MappingAnnotationWriter(),
                                        ParameterAnnotationWriter(frameworkAnnotations),
                                        beanValidationFactory,
                                        javaDocWriter
                                ),
                                frameworkAnnotations,
                                beanValidationFactory,
                                DefaultImportFilter()
                        ),
                        DataTypeWriter(
                                options,
                                headerWriter,
                                beanValidationFactory),
                        StringEnumWriter(headerWriter),
                        InterfaceDataTypeWriter(
                                options,
                                headerWriter,
                                javaDocWriter
                        ),
                        AnnotationProcessorFileHandler(filer)
                )

                writer.write(api)
            } catch (e: Exception) {
//                log.error("processing failed!", e)
                throw e
            }
        }

        return false
    }

    @Suppress("UNCHECKED_CAST")
    private fun convertOptions(processorOptions: Map<String, *>): ApiOptions {
        val options = OptionsConverter().convertOptions(processorOptions as Map<String, Any>)
        options.validate()
        return options
    }
}