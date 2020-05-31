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

package com.github.hauner.openapi.core.framework

import com.github.hauner.openapi.core.model.parameters.Parameter
import com.github.hauner.openapi.spring.model.datatypes.DataType
import com.github.hauner.openapi.core.parser.Parameter as ParserParameter

/**
 * factory for framework model objects.
 */
interface Framework {

    /**
     * create a model query parameter.
     *
     * @param parameter an OpenAPI query parameter
     * @param DataType data type of the parameter
     * @return a query {@Parameter}
     */
    Parameter createQueryParameter(ParserParameter parameter, DataType dataType)

    /**
     * create a model header parameter.
     *
     * @param parameter an OpenAPI header parameter
     * @param DataType data type of the parameter
     * @return a header {@Parameter}
     */
    Parameter createHeaderParameter(ParserParameter parameter, DataType dataType)

    /**
     * create a model cookie parameter.
     *
     * @param parameter an OpenAPI cookie parameter
     * @param DataType data type of the parameter
     * @return a cookie {@Parameter}
     */
    Parameter createCookieParameter(ParserParameter parameter, DataType dataType)

    /**
     * create a model path parameter.
     *
     * @param parameter an OpenAPI path parameter
     * @param DataType data type of the parameter
     * @return a path {@Parameter}
     */
    Parameter createPathParameter(ParserParameter parameter, DataType dataType)

    /**
     * create a model multipart parameter.
     *
     * @param parameter an OpenAPI multipart parameter
     * @param DataType data type of the parameter
     * @return a multipart {@Parameter}
     */
    Parameter createMultipartParameter(ParserParameter parameter, DataType dataType)

    /**
     * create a model additional parameter.
     *
     * @param parameter an OpenAPI additional parameter
     * @param DataType data type of the parameter
     * @return an additional {@Parameter}
     */
    Parameter createAdditionalParameter(ParserParameter parameter, DataType dataType)

}
