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
import io.openapiprocessor.core.parser.MultipartParameter as MultipartParserParameter
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.core.parser.RequestBody as ParserRequestBody

/**
 * Spring model factory.
 *
 * @author Martin Hauner
 */
class SpringFramework: FrameworkBase() {

    @Override
    override fun createQueryParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        return QueryParameter (
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated())
    }

    override fun createMultipartParameter(parameter: ParserParameter, dataType: DataType): Parameter {
        val mpp = parameter as MultipartParserParameter

        return MultipartParameter(
            parameter.getName(),
            dataType,
            parameter.isRequired(),
            parameter.isDeprecated(),
            parameter.description,
            mpp.contentType
        )
    }

    override fun createRequestBody(contentType: String, requestBody: ParserRequestBody, dataType: DataType): RequestBody {
        return RequestBody(
            "body",
            contentType,
            dataType,
            requestBody.getRequired(),
            false,
            requestBody.description)
    }
}
