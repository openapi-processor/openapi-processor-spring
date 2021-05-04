/*
 * Copyright 2020 the original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.openapiprocessor.spring.processor

import io.openapiprocessor.core.framework.FrameworkBase
import io.openapiprocessor.core.model.datatypes.DataType
import io.openapiprocessor.core.model.parameters.Parameter
import io.openapiprocessor.core.parser.MultipartParameter as MultipartParserParameter
import io.openapiprocessor.core.parser.Parameter as ParserParameter
import io.openapiprocessor.spring.model.parameters.MultipartParameter
import io.openapiprocessor.spring.model.parameters.QueryParameter

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
}
