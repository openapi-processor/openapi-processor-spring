package io.openapiprocessor.spring.annotationprocessor

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class OpenAPISpringProcessor(
        val apiPath: String,
        val mapping: String = ""
)
