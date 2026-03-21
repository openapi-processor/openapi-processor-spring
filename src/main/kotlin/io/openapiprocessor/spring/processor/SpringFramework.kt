/*
 * Copyright 2020 https://github.com/openapi-processor/openapi-processor-spring
 * PDX-License-Identifier: Apache-2.0
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.spring.model.RequestBody
import io.openapiprocessor.spring.model.parameters.MultipartParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter
import io.openapiprocessor.core.openapi.Parameter as OpenApiParameter
import io.openapiprocessor.core.openapi.RequestBody as OpenApiRequestBody
import io.openapiprocessor.core.parser.MultipartParameter as ParserMultipartParameter

/**
 * Spring model factory.
 *
 * @author Martin Hauner
 */
class SpringFramework: FrameworkBase() {

    @Override
    override fun createQueryParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        return QueryParameter (
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated())
    }

    override fun createMultipartParameter(parameter: OpenApiParameter, dataType: DataType): Parameter {
        val mpp = parameter as ParserMultipartParameter

        return MultipartParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description,
            mpp.contentType
        )
    }

    override fun createRequestBody(contentType: String, requestBody: OpenApiRequestBody, dataType: DataType): RequestBody {
        return RequestBody(
            "body",
            contentType,
            dataType,
            requestBody.getRequired(),
            false,
            requestBody.description)
    }
}
